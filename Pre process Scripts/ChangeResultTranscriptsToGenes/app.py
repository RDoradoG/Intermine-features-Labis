import sys, getopt, argparse

from library import *
from gff3 import gff3
from orthologs import orthologs

#from BioProject import BioProject

class app():
	"""docstring for app"""
	def __init__(self):
		arg                 = self.getArguments()
		self.orthologs_file = arg.orthologs_file
		self.gff3_files     = arg.gff3.split(',')
		self.gff3s          = []
		self.onlys          = ['mRNA', 'tRNA', 'CDS']

	def getArguments(self):
		"""Get the arguments of the command executed

		Get the arguments and their values of the program execution

		Returns:
			Object -- The arguments with values
		"""
		parser = argparse.ArgumentParser()
		parser.add_argument("-o", "--orthologs_file", help = "The result of orthofinder")
		parser.add_argument("-g", "--gff3", help = "The ggf3 files of the organism in the same order than the result of orthofiner separate with comas")

		data   = parser.parse_args()

		if isEmpty(data.gff3) or isEmpty(data.orthologs_file):
			print("Is required the orthologs result file and the gff3 of the organism accession")
			parser.print_help()
			exit()

		return data

	def readGFF3Files(self):
		for gff3_file in self.gff3_files:
			Gff3 = gff3(gff3_file, self.onlys)
			self.gff3s.append(Gff3)

	def readOrthologsFile(self):
		self.Orthologs = orthologs(self.orthologs_file)

	def changeTranscriotToGene(self):
		self.Orthologs.findGenesofTranscripts(self.gff3s)

	def writeFile(self):
		self.Orthologs.writeResultFiles(self.orthologs_file + '.genes', 'genes')

app = app()
app.readGFF3Files()
app.readOrthologsFile()
app.changeTranscriotToGene()
app.writeFile()