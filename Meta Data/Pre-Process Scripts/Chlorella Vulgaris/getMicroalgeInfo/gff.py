from library import *

class gff():
	"""docstring for gff"""
	def __init__(self, arg):
		self.gff_file = arg.gff_file
		self.only     = ['gene', 'mRNA', 'CDS']
		self.execute  = False
		if arg.uniprot or arg.go_annotation or arg.kegg or arg.eggNOG or arg.Annotation_info:
			self.execute = True

	def getobjectResult(self):
		if self.execute:
			self.elements = self.readFile(self.only)

	def readFile(self, only):
		elements = {}
		with open(self.gff_file) as gff_file:
			for line in gff_file:
				line = line.strip()
				cols = line.split("\t")
				if cols[2] in only:
					element = self.getDataElement(cols)
					elements[element['ID']] = element

		return elements

	def getDataElement(self, cols):
		element         = {}
		element['type'] = cols[2]
		infos           = cols[8].split(";")
		for info in infos:
			if not isEmpty(info):
				data         = info.split("=")
				if len(data) > 1:
					if data[0] in ['ID', "Name", "Parent"]:
						element[data[0]] = data[1]

		return element

	def searchGeneWithTrnscript(self, transcript):
		if 'Parent' in self.elements[transcript]:
			return self.elements[transcript]['Parent']

		return ''

	def getGFFFile(self):
		return self.gff_file		
