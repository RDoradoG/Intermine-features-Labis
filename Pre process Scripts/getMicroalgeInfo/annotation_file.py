import sys, re
from library import *

from progressPercent import progressPercent
from bufferWritter import bufferWritter
from gff import gff

class annotation_file(object):
    """docstring for annotation_file"""
    def __init__(self, arg, gff):
        self.annotation_file       = arg.annotation_file
        self.folder_name           = arg.folder_name + '/annotationInfo'
        self.TranscriptDefLineFile = self.folder_name + '/Transcript_defLine.txt'
        self.gff                   = gff
        self.to_execute            = False
        self.writeFile             = arg.Annotation_info
        if arg.kegg or arg.go_annotation or arg.eggNOG or arg.Annotation_info:
            self.to_execute = True

        self.bufferWritter   = bufferWritter('')

    def getNumberOfLines(self):
        with open(self.annotation_file) as annotation_file:
            return sum(1 for i in annotation_file)

    def getFileData(self):
        data = []
        with open(self.annotation_file) as annotation_file:
            for line in annotation_file:
                line = line.strip()
                cols = line.split("\t")
                gene_id = self.getGene(cols[0])
                if isEmpty(gene_id):
                    print("transcript error: " + cols[0])
                    continue

                row  = {
                    'transcript id': cols[0],
                    'gene id': gene_id,
                    'seed ortholog': cols[1],
                    'evalue': cols[2],
                    'score': cols[3],
                    'predicted name': cols[4],
                    'GO terms': cols[5],
                    'KEGG KO': cols[6],
                    'BiGG reactions': cols[7],
                    'tax scope': cols[8],
                    'eggNOG OGs': cols[9],
                    'best OG': cols[10],
                    'COG Cat.': cols[11],
                    'eggNOG HMM Desc': cols[12]
                }
                data.append(row)

        return data

    def getobjectResult(self):
        if self.to_execute:
            self.data = {
                'lines': self.getNumberOfLines(),
                'data': self.getFileData()
            }

        else:
            self.data = {
                'lines': 0,
                'data': []
            }

        return self.data

    def getGene(self, transcript):
        return self.gff.searchGeneWithTrnscript(transcript)

    def execute(self):
        if self.writeFile:
            createFolder(self.folder_name)
            self.writeTranscriptDefLine()

    def writeTranscriptDefLine(self):
        self.bufferWritter.setActiveFile(self.TranscriptDefLineFile)
        for row in self.data['data']:
            defLine = row['eggNOG HMM Desc']
            if not isEmpty(defLine) and not defLine == 'NA':
                line = row['transcript id'] + "\t" + defLine + "\n"
                self.bufferWritter.printLine(line)

        self.bufferWritter.finishBuffering()