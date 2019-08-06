import time, datetime

from progressPercent import progressPercent
from annotation_file import annotation_file
from bufferWritter import bufferWritter

from library import *

class goAnnotation(object):
    """docstring for goAnnotation"""
    def __init__(self, arg, date, annotations):
        self.to_execute      = arg.go_annotation
        self.obo_file        = arg.obo_file
        self.taxon           = arg.taxon
        self.date            = self.getDateSctring(date)
        self.folder_name     = arg.folder_name + '/go-annotation'
        self.result_file     = self.folder_name + '/go-annotation.txt'
        self.acpects         = {
            'external': 'E',
            'biological_process': 'P',
            'molecular_function': 'F',
            'cellular_component': 'C'
        }
        self.annotations     = annotations
        self.bufferWritter   = bufferWritter(self.result_file)
        self.progressPercent = progressPercent(2)
        self.progressPercent.setPreviousMesssage("go-annotation: ")

    def execute(self):
        if self.to_execute:
            createFolder(self.folder_name)
            self.go_terms = self.readOBOFile()
            self.getFileData()

    def getDateSctring(self, date):
        return date.strftime("%Y%m%d")

    def readOBOFile(self):
        objKey   = ['id', 'name', 'namespace', 'def']
        go_term  = {}
        save     = False
        go_terms = []
        with open(self.obo_file) as obo_file:
            for line in obo_file:
                line = line.strip()
                if line == "[Typedef]":
                    break

                if line == "[Term]":
                    save = True
                    if not isEmptyObject(go_term):
                        go_terms.append(go_term)

                    go_term = {}


                else:
                    if save and line != '':
                        elements = line.split(": ", 1)
                        key      = elements[0]
                        value    = elements[1]
                        if key in objKey:
                            if key == 'namespace':
                                go_term['aspect'] = self.acpects[value]

                            go_term[key] = value

                        else:
                            if self.verifyKeyinObjetc(go_term, key):
                                go_term[key].append(value)

                            else:
                                go_term[key] = []
                                go_term[key].append(value)

        return go_terms

    def verifyKeyinObjetc(self, obj, key):
        for element in obj:
            if element == key:
                return True

        return False

    def getFileData(self):
        line_num = 0
        for row in self.annotations['data']:
            line_num += 1
            if row['GO terms'] != '':
                goTerms =  row['GO terms'].split(',')
                for goTerm in goTerms:
                    go_term =  self.getGoTerm(goTerm)
                    if go_term == 'no exists':
                        print("----")
                        print("Go term does not exists in obo file: " + goTerm)
                        print("----")

                    else:
                        self.printResult(row['gene id'], goTerm, go_term['aspect'], go_term['def'])

            if self.bufferWritter.isEmpty():
                self.progressPercent.calculatePercent(line_num, self.annotations['lines'])

        self.bufferWritter.finishBuffering()
        self.progressPercent.finishPercent()

    def printResult(self, gene_id, go_term, aspect, defline):
        line = "phycomine\t" + gene_id + "\t" + gene_id + "\t\t" + go_term + "\t\tIC\t\t" + aspect + "\t" + defline + "\t" + gene_id + "\tgene\ttaxon:" + self.taxon + "\t" + self.date + "\tphycomine\n"
        self.bufferWritter.printLine(line)

    def getGoTerm(self, go_term_id):
        for go_term in self.go_terms:
            if go_term['id'] == go_term_id:
                return go_term

        for go_term in self.go_terms:
            if self.verifyKeyinObjetc(go_term, 'alt_id'):
                for atl_id in go_term['alt_id']:
                    if atl_id == go_term_id:
                        return go_term

        return 'no exists'