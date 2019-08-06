import sys, getopt, os, argparse

class genesGFF(object):
	"""docstring for genesGFF"""
	def __init__(self):
		arguments = self.getArguments()
		self.gff  = arguments.gff

	def getArguments(self):
		parser = argparse.ArgumentParser()
		parser.add_argument("-g", "--gff", help = "The GFF3 File.")

		data   = parser.parse_args()

		if self.isEmpty(data.gff):
			parser.print_help()
			exit()

		return data

	def getGenes(self):
		with open(self.gff) as file:
			for line in file:
					cols = line.strip().split('\t')
					if cols[2] == 'gene':
						opts = cols[8].split(';')
						for opt in opts:
							parts = opt.split('=')
							key = parts[0]
							value = parts[1]
							if key == 'ID':
								print(value)

	def isEmpty(self, var):
		"""Verify if a variable is empty
		
		Verify if a variable is empty
		
		Arguments:
			var {all} -- Variable
		
		Returns:
			[boolean] -- If the variable is 'None' or '' or the length of it is 0
		"""
		return var == None or var == '' or len(var) == 0


GenesGFF = genesGFF();
GenesGFF.getGenes()
