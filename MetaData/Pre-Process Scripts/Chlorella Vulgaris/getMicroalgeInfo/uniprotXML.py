import re

from xml.dom import minidom

from gff import gff

from library import *

class uniprotXML(object):
    """docstring for uniprotXML"""
    def __init__(self, arg, argumentline, gff):
        self.to_execute     = arg.uniprot
        self.fasta_proteins = arg.fasta_proteins
        self.fasta_uniprot  = arg.fasta_uniprot
        self.xml_uniprot    = arg.xml_uniprot
        self.step_uniprot   = arg.step_uniprot
        self.argumentline   = argumentline
        self.taxon          = arg.taxon
        self.gff            = gff
        if self.to_execute:
            self.folder_name    = arg.folder_name + '/uniprot'
            self.blastResult    = self.folder_name + '/result_blast'
            self.resultBlastTab = self.folder_name + '/result_blast_tab'
            self.xmlResult      = self.folder_name + '/' + arg.taxon + '_uniprot_sprot.xml'

    def execute(self):
        if self.to_execute:
            if self.step_uniprot == '1':
                createFolder(self.folder_name)
                self.makeBlastdb()
                self.blastp()

            if self.step_uniprot == '2':
                self.getResultOfBlast()
                self.prepareXML()

    def makeBlastdb(self):
        command = 'makeblastdb -in ' + self.fasta_proteins + ' -dbtype prot'
        cmdline(command)

    def blastp(self):
        command     ="blastp -db " + self.fasta_proteins + " -query " + self.fasta_uniprot + " -evalue 1e-5 -num_threads 2 -outfmt '7 std qlen slen' > " + self.blastResult
        qsubCommand = 'echo "' + command + '" | qsub -V -cwd -q all.q -pe threads 2 -o getUniprotXML.out -N getUniprotXML'
        cmdline(qsubCommand)
        command2 = self.argumentline + " --uniprot --step 2 --fasta_proteins " + self.fasta_proteins + " --fasta_uniprot " + self.fasta_uniprot + " --xml_uniprot " + self.xml_uniprot + " --taxon " + self.taxon + " --gff " + self.gff.getGFFFile()
        qsub2Command = 'echo "' + command2 + '" | qsub -V -cwd -q all.q -pe threads 1 -o getUniprotXML.out -N setUniprotXML -hold_jid getUniprotXML'
        cmdline(qsub2Command)
        #self.printExecuteUser(self.argumentline + " --uniprot --step 2 --fasta_proteins " + self.fasta_proteins + " --fasta_uniprot " + self.fasta_uniprot + " --xml_uniprot " + self.xml_uniprot + " --taxon " + self.taxon + " --gff " + self.gff.getGFFFile())

    def getResultOfBlast(self):
        with open(self.blastResult) as allFile:
            merge_total     = False
            has_merge_total = True
            for line in allFile:
                line = line.strip()
                if line.startswith('# Query:'):
                    line_spasep     = line.split(" ")
                    actual_query_id = line_spasep[2]

                if line.startswith('# BLASTP'):
                    if not has_merge_total:
                        if row_parcial['filled']:
                            self.verifyMergeParcialExists(row_parcial, actual_query_id)

                    merge_total     = False
                    has_merge_total = False
                    row_parcial     = {
                        'per min identity': 80,
                        'size diff': 10,
                        'filled': False,
                        'query id': '',
                        'subject id': ''
                    }

                if line.startswith('#'):
                    continue

                lineTab = line.split("\t")
                row     = {
                    'query id': lineTab[0],
                    'subject id': lineTab[1],
                    'per identity': lineTab[2],
                    'alignment length': lineTab[3],
                    'mismatches': lineTab[4],
                    'gap opens': lineTab[5],
                    'q. start': lineTab[6],
                    'q. end': lineTab[7],
                    's. start': lineTab[8],
                    's. end': lineTab[9],
                    'evalue': lineTab[10],
                    'bit score': lineTab[11],
                    'query length': lineTab[12],
                    'subject length': lineTab[13]
                }
                merge_total = self.verifyMergeTotal(row)
                if merge_total:
                    has_merge_total = True
                    merge_total     = False
                    self.printResult(row['query id'], row['subject id'])

                else:
                    row_parcial = self.verifyMergeParcial(row, row_parcial)

    def verifyMergeTotal(self, row):
        if row['query length'] != row['subject length']:
            return False

        if row['per identity'] != '100.00':
            return False

        if row['query length'] != row['alignment length']:
            return False

        return True

    def verifyMergeParcial(self, row, row_parcial):
        per_identity = float(row['per identity'])
        size_diff    = self.calculateSizeDiff(int(row['query length']), int(row['subject length']))
        if (per_identity > row_parcial['per min identity']) and (size_diff < row_parcial['size diff']):
            row_parcial['per min identity'] = per_identity
            row_parcial['size diff']        = size_diff
            row_parcial['query id']         = row['query id']
            row_parcial['subject id']       = row['subject id']
            row_parcial['filled']           = True

        return row_parcial

    def verifyMergeParcialExists(self, row, actual_query_id):
        if row['filled']:
            self.printResult(row['query id'], row['subject id'])

        else:
            self.printResult(actual_query_id, '')

    def printResult(self, query_id, subject_id):
        line = query_id + "\t" + subject_id + "\n"
        with open(self.resultBlastTab, 'a') as resultBlastTab:
            resultBlastTab.write(line)

    def calculateSizeDiff(self, query, subject):
        if query > subject:
            diff = query - subject
            big  = query

        else:
            diff = subject - query
            big  = subject

        if diff > 0:
            return (diff * 100) / big

        else:
            return 0

    def getGene(self, transcript):
        #return re.sub(self.rule_regex, '', transcript)
        return self.gff.searchGeneWithTrnscript(transcript)

    def prepareXML(self):
        key_table  = list(())
        with open(self.resultBlastTab) as f:
            for line in f:
                line           = line.strip()
                columns        = line.split("\t")
                all_first_data = columns[0].split("|")
                key_table.append({
                    "accession": all_first_data[1],
                    "name":  all_first_data[2],
                    #"newKey": columns[1]
                    "newKey": self.getGene(columns[1])
                })

        mydoc       = minidom.parse(self.xml_uniprot)
        all_uniprot = mydoc.getElementsByTagName('uniprot')
        uniprot     = all_uniprot[0]
        entrys      = uniprot.getElementsByTagName('entry')
        for entry in entrys:
            accessions = entry.getElementsByTagName('accession')
            genes      = entry.getElementsByTagName('gene')
            kewnames   = list(())
            for accession in accessions:
                for row in key_table:
                    if row["accession"] == accession.firstChild.data:
                        kewnames.append(row["newKey"])

            for gene in genes:
                geneNames = gene.getElementsByTagName('name')
                ORFS      = list(())
                for genaName in geneNames:
                    if genaName.attributes['type'].value == "ORF":
                        gene.removeChild(genaName)

                for keyname in kewnames:
                    newORF     = mydoc.createElement("name")
                    newORF.setAttribute("type", "ORF")
                    label      = keyname.replace("\r\n", "")
                    newORFText = mydoc.createTextNode(label)
                    newORF.appendChild(newORFText)
                    gene.appendChild(newORF)

        file_handle = open(self.xmlResult, "w")
        mydoc.writexml(file_handle)
        file_handle.close()

    def printExecuteUser(self, command):
        print("--------------------------------------------")
        print("Please, execute this code after 'getUniprotXML' process finish:")
        print(command)
        print("--------------------------------------------")