import sys
import re

class gene_pathway:
	def __init__(self, newId):
		self.id = newId
		self.pathways = []
		self.splitStr = ' '

	def addPathwayForString(self, pathway):
		pathwaystr = pathway.split(self.splitStr)
		for path in pathwaystr:
			if (not self.searchPathway(path)) :
				self.addPathway(path)

	def addPathway(self, pathway):
		self.pathways.append(pathway)

	def searchPathway(self, pathway):
		for path in self.pathways:
			if (path == pathway):
				return True
		return False

	def getStringPathways(self):
		return self.splitStr.join(self.pathways)

def searchIdGene(genes, id):
	for i in range(len(genes)):
		if (genes[i].id == id):
			return i
	return -1

keggFile = sys.argv[1]
keggNewFile = sys.argv[2]

allGenes = []

with open(keggFile) as keggFileText:
	for keggLine in keggFileText:
		keggLine = keggLine.replace("\n", '')
		columns = keggLine.split("\t")
		i = searchIdGene(allGenes, columns[0])
		if (i < 0):
			i = len(allGenes)
			allGenes.append(gene_pathway(columns[0]))
		else:
			print(allGenes[i].id + "\n")
		allGenes[i].addPathwayForString(columns[1])


#newFile = open(keggNewFile,"w") 

for gene in allGenes:
	line = gene.id + "\t" + gene.getStringPathways() + "\n"
	#newFile.write(line)

#newFile.close() 
