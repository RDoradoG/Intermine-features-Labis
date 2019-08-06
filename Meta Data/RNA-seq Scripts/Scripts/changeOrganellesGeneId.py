import sys, getopt, argparse, os

class Gff(object):
	"""docstring for Gff"""
	def __init__(self, file):
		self.file = file
		self.genes = {}

	def getBioentites(self):
		return self.genes

	def readFile(self):
		with open(self.file) as file:
			for line in file:
				cols = line.strip().split('\t')
				if cols[2] == 'gene':
					infos = cols[8].split(';')
					for info in infos:
						data = info.split('=')
						if data[0] == 'ID':
							value = data[1]
							break

					oldValue = value.replace('ch-', '').replace('mt-', '')

					self.genes[oldValue] = value

class FixFile(object):
	"""docstring for FixFile"""
	def __init__(self, file):
		self.file = file
		self.newFile = file + '.new'
		self.genes = []
		self.colNumber = 0
		self.separator = '\t'

	def setGenes(self, genes):
		self.genes = genes

	def fixFile(self):
		header = True
		with open(self.file) as file:
			newFile = open(self.newFile, 'w')
			for line in file:
				if header:
					header = False
					newFile.write(line)
					continue

				cols = line.strip().split(self.separator)
				oldGene = cols[self.colNumber]
				for gene in self.genes:
					if oldGene in gene:
						oldGene = gene[oldGene]
						break

				cols[self.colNumber] = oldGene

				newFile.write(self.separator.join(cols) + '\n')

			newFile.close()

def isEmpty(var):
	return var == None or var == '' or len(var) == 0
		
parser = argparse.ArgumentParser()
parser.add_argument("-g", "--gff", help = "The GFF3 file, multiple files separate by ','.")
parser.add_argument("-f", "--folder", help = "The file to repair.")
parser.add_argument("-p", "--prefix", help = "The prefix of the files.")

data   = parser.parse_args()

if isEmpty(data.folder) and isEmpty(data.gff):
	parser.print_help()
	exit()

gffFiles = data.gff.split(',')
genes = []

for gffFile in gffFiles:
	gff = Gff(gffFile)
	gff.readFile()
	genes.append(gff.getBioentites())

#print(genes)

numFile = 0
for root, dirs, files in os.walk(data.folder):
	for filename in files:
		numFile = numFile + 1
		print((numFile * 100) / len(files))
		if data.prefix in filename:
			if filename == 'I-ExpressionValues-PRJNA373014.txt':
				fixFile = FixFile(data.folder + filename)
				fixFile.setGenes(genes)
				fixFile.fixFile()