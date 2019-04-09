from library import *

class gff3():
	"""docstring for gff3"""
	def __init__(self, file, only):
		self.elements = self.readFile(file, only)
		
	def readFile(self, file, only):
		elements = {}
		with open(file) as gff_file:
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

	def getElement(self, id):
		return self.elements[id]

	def getParentElement(self, id):
		if 'Parent' in self.elements[id]:
			return self.elements[id]['Parent']

		return ''

	def getNameElement(self, id):
		return self.elements[id]['Name']