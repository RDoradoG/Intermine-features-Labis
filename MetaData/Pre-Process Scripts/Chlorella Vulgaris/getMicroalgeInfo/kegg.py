from six.moves.urllib.request import urlopen

from progressPercent import progressPercent
from annotation_file import annotation_file
from bufferWritter import bufferWritter

from library import *

class kegg(object):
    """docstring for kegg"""
    def __init__(self, arg, annotations):
        self.to_execute      = arg.kegg
        self.annotations     = annotations
        self.pathways        = {}
        self.keggObj         = {}
        self.keggs           = {}
        self.folder_name     = arg.folder_name + '/kegg'
        self.kegg_file       = self.folder_name + '/unique.tab'
        self.pathway_file    = self.folder_name + '/map_title.tab'
        self.pre_str_pathway = 'map'
        self.url_ko          = 'http://rest.kegg.jp/link/pathway/ko:'
        self.url_pathway     = 'http://rest.kegg.jp/get/pathway:'
        self.bufferWritter   = bufferWritter('')
        self.progressPercent = progressPercent(2)

    def execute(self):
        if self.to_execute:
            createFolder(self.folder_name)
            self.getFileData()
            self.getPathwayNames()
            self.printKeggResult()
            self.printPathways()

    def printKeggResult(self):
        self.bufferWritter.setActiveFile(self.kegg_file)
        for key in self.keggObj:
            kos = []
            if len(self.keggObj[key]) > 0:
                for pathway_id in self.keggObj[key]:
                    kos.append(','.join(pathway_id))

                values             = ','.join(kos)
                line               = key + "\t" + values + "\n"
                self.bufferWritter.printLine(line)

        self.bufferWritter.finishBuffering()

    def printPathways(self):
        self.bufferWritter.setActiveFile(self.pathway_file)
        for pathway_id in self.pathways:
            line = pathway_id + "\t" + self.pathways[pathway_id] + "\n"
            self.bufferWritter.printLine(line)

        self.bufferWritter.finishBuffering()

    def getPathwayNames(self):
        self.progressPercent.setPreviousMesssage("extracting pathways name: ")
        counter = 0
        for key in self.pathways:
            counter            += 1
            self.pathways[key] = self.searchNameInContent(self.url_pathway + self.pre_str_pathway + key)
            self.progressPercent.calculatePercent(counter, len(self.pathways))

        self.progressPercent.finishPercent()

    def searchNameInContent(self, url):
        content = executeURL(url)
        lines   = content.split("\n")
        for line in lines:
            if line.startswith('NAME'):
                cols = line.split('        ')
                return cols[1]

    def getFileData(self):
        line_num = 0
        self.progressPercent.setPreviousMesssage("extracting pathways id: ")
        for row in self.annotations['data']:
            line_num += 1
            if not row['KEGG KO'] == '':
                keggs                 = row['KEGG KO'].split(',')
                gene_id               = row['gene id']
                self.keggObj[gene_id] = []
                for kegg in keggs:
                    if not self.verifyIfKeggExists(kegg):
                        self.keggs[kegg] = self.getPathwayOfKo(self.url_ko + kegg)

                    if len(self.keggs[kegg]) > 0:
                        self.keggObj[gene_id].append(self.keggs[kegg])

            self.progressPercent.calculatePercent(line_num, self.annotations['lines'])

        self.progressPercent.finishPercent()

    def verifyIfKeggExists(self, ko):
        for kegg in self.keggs:
            if kegg == ko:
                return True

        return False

    def getPathwayOfKo(self, url):
        content  = executeURL(url)
        lines    = content.split("\n")
        pathways = []
        for line in lines:
            if line != '':
                columns = line.split("\t")
                pathway = self.extarctPathwwayId(columns[1])
                if not pathway == 'no exists':
                    self.pathways[pathway] = ''
                    pathways.append(pathway)

        return pathways

    def extarctPathwwayId(self, value):
        res = value.split(":", 1)
        if res[1].startswith(self.pre_str_pathway):
            aux = res[1].split(self.pre_str_pathway)
            return aux[1]

        return 'no exists'