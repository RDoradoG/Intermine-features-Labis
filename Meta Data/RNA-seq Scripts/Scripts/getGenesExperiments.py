import sys, getopt, subprocess, os, argparse
from os import walk

class GenesExperiments():
	"""docstring for GenesExperiments"""
	def __init__(self):
		data = self.getArguments()
		self.files = self.getFiles(data)
		self.genes = {}

	def getFiles(self, data):
		files = []
		if not self.isEmpty(data.file):
			files = data.file.split(',')

		if not self.isEmpty(data.directive):
			for (dirpath, dirnames, filenames) in walk(data.directive):
				files.extend(filenames)
				break

		return files

	def execute(self):
		for file in self.files:
			self.getGenesExperimentFromFile(file)

		for gene in self.genes:
			self.genes[gene] = self.getUniqueArray(self.genes[gene])

	def printResult(self):
		for gene in self.genes:
			line = self.getLineGene(gene)
			print(gene + line)

	def getLineGene(self, gene):
		line = ''
		for experiment in self.genes[gene]:
			line = line + "\t" + experiment

		return line

	def getUniqueArray(self, setValues):
		used = set()
		return [x for x in setValues if x not in used and (used.add(x) or True)]

	def getGenesExperimentFromFile(self, file):
		with open(file) as allFile:
			for line in allFile:
				line = line.strip()
				cols = line.split("\t")
				gene = cols[0]
				if gene not in self.genes:
					self.genes[gene] = []

				self.genes[gene].append(cols[1]) 

	def getArguments(self):
		"""Get the arguments of the command executed
		
		Get the arguments and their values of the program execution
		
		Returns:
			Object -- The arguments with values
		"""
		parser = argparse.ArgumentParser()
		parser.add_argument("-f", "--file", help = "The files separte by coma ','")
		parser.add_argument("-d", "--directive", help = "The folder with th files")

		data   = parser.parse_args()

		if self.isEmpty(data.file) and self.isEmpty(data.directive):
			print("Is required the files or the directives to execute")
			parser.print_help()
			exit()

		return data


	def isEmpty(self, var):
		"""Verify if a variable is empty
		
		Verify if a variable is empty
		
		Arguments:
			var {all} -- Variable
		
		Returns:
			[boolean] -- If the variable is 'None' or '' or the length of it is 0
		"""
		return var == None or var == '' or len(var) == 0

genesExperiments = GenesExperiments()
genesExperiments.execute()
genesExperiments.printResult()
