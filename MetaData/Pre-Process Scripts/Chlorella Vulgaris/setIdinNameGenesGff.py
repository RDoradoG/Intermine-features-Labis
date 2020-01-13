import sys, getopt, os, argparse

class app():
	"""docstring for app"""
	def __init__(self, gff_file):
		self.gff_file = gff_file
		self.readFile('gene')

	def readFile(self, only):
		with open(self.gff_file) as gff_file:
			for line in gff_file:
				line = line.strip()
				cols = line.split("\t")
				if cols[2] == only:
					cols[8] = self.getDataElementString(self.getDataElement(cols[8]))
					newLine = "\t".join(cols)
					with open(self.gff_file + '_new', 'a') as activeFile:
						activeFile.write(newLine + "\n")

				else:
					with open(self.gff_file + '_new', 'a') as activeFile:
						activeFile.write(line + "\n")
	
	def getDataElement(self, cols):
		element         = {}
		infos           = cols.split(";")
		for info in infos:
			if not self.isEmpty(info):
				data         = info.split("=")
				if len(data) > 1:
					element[data[0]] = data[1]

		element['Name'] = element['ID']
		return element

	def getDataElementString(self, element):
		info = []
		for key in element:
			info.append(key + "=" + element[key])

		return ';'.join(info)

	def isEmpty(self, var, con = True):
		return var == None or var == '' or len(var) == 0

App = app('organelles.gff3')