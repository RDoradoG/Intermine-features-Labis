from library import *

from gff3 import gff3

class orthologs():
	"""docstring for orthologs"""
	def __init__(self, file):
		self.elements = self.readFile(file)

	def readFile(self, file):
		elements = []
		head = True
		with open(file) as ortholog_file:
			for line in ortholog_file:
				if head:
					self.firstLine = line
					head = False
					continue

				element                = {}
				line                   = line.strip()
				cols                   = line.split("\t")
				element['ID']          = cols[0]
				element['transcripts'] = []
				element['genes']       = []
				for transcript_list in cols[1:]:
					if isEmpty(transcript_list):
						transcripts = []
						
					else:
						transcripts = transcript_list.split(", ")
						
					element['transcripts'].append(transcripts)

				elements.append(element)

		return elements
		
	def findGenesofTranscripts(self, gff3s):
		for element in self.elements:
			posGff3 = 0
			for transcripts in element['transcripts']:
				genes = []
				for transcript in transcripts:
					genes.append(gff3s[posGff3].getParentElement(transcript))

				posGff3 += 1
				element['genes'].append(genes)

	def writeResultFiles(self, new_file, key):
		with open(new_file, 'a') as activeFile:
			activeFile.write(self.firstLine)
			for element in self.elements:
				transcript_lines = []
				for transcripts in element[key]:
					transcript_lines.append(','.join(transcripts))

				transcript_line = '\t'.join(transcript_lines)
				activeFile.write(element['ID'] + '\t' + transcript_line + '\n')