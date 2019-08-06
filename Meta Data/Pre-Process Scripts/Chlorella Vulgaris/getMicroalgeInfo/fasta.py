from library import *

from progressPercent import progressPercent
from bufferWritter import bufferWritter
from gff import gff

class fasta():
	"""docstring for fasta"""
	def __init__(self, arg, gff):
		self.file            = arg.fasta_proteins
		self.gff             = gff
		self.execute         = arg.Annotation_info
		self.keys            = []
		self.folder_name     = arg.folder_name + '/annotationInfo'
		self.proteinGeneFile = self.folder_name + '/proteinGene.txt'
		self.bufferWritter   = bufferWritter('')

	def getobjectResult(self):
		if self.execute:
			createFolder(self.folder_name)
			self.transcripts = self.readFile()
			self.writeProteinGeneFile(self.keys)

	def readFile(self):
		elements = {}
		key = ''
		with open(self.file) as fasta_file:
			for line in fasta_file:
				#line = line.strip()
				if line.startswith('>'):
					if key != '':
						self.keys.append(key)
						elements[key] = sequence

					line     = line.strip()
					cols     = line.split(' ')
					key      = cols[0][1:]
					sequence = ''
				else:
					sequence += line

		return elements

	def writeProteinGeneFile(self, transcriptKeys):
		self.bufferWritter.setActiveFile(self.proteinGeneFile)
		line = "Protein\tGene\n"
		self.bufferWritter.printLine(line)
		for key in transcriptKeys:
			gene = self.getGene(key)
			if not isEmpty(gene):
				line = key + "\t" + gene + "\n"
				self.bufferWritter.printLine(line)

		self.bufferWritter.finishBuffering()

	def getGene(self, transcript):
		return self.gff.searchGeneWithTrnscript(transcript)

