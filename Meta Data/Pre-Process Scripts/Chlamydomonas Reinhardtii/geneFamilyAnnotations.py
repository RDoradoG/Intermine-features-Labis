import sys, getopt, subprocess, os, argparse

class geneFamilyAnnotations():
	"""docstring for geneFamilyAnnotations"""
	def __init__(self, type, annotations, gene_family_file, annotations_plntfdb, output):
		self.type                = type
		self.annotations         = annotations
		self.gene_family_file    = gene_family_file.split(',')
		self.annotations_plntfdb = annotations_plntfdb
		self.output              = output
		self.nogAnnotations      = {}
		self.plnAnnotations      = {}

	def verifyType(self, value):
		if self.type == value:
			return True
		return False

	def getAnnotationsEggnog(self):
		for root, dirs, files in os.walk(self.annotations):  
			for filename in files:
				with open(self.annotations + filename) as allFile:
					for line in allFile:
						line = line.strip()
						cols = line.split("\t")
						self.nogAnnotations[cols[1].replace('ENOG41', '')] = {
							"dataset" : cols[0],
							"annotation": cols[5].replace('NA', '')
						}

	def getAnnotationsPlnTFBD(self):
		with open(self.annotations_plntfdb) as allFile:
			for line in allFile:
				line = line.strip()
				cols = line.split("\t")
				self.plnAnnotations[cols[0]] = cols[2].replace('NULL', '')

	def writeNewFileEgggnog(self):
		errorCount = 0
		errors = []
		toWrite = {}
		for family_file in self.gene_family_file:
			header = True
			with open(family_file) as allFile:
				for line in allFile:
					if header:
						header = False

					else:
						line = line.strip()
						cols = line.split("\t")
						families = cols[2].split(',')
						for family in families:
							if family in self.nogAnnotations:
								toWrite[family] = family + "\t" + self.nogAnnotations[family]['dataset'] + "\t" + self.nogAnnotations[family]['annotation'] + "\n"
							else:
								errorCount = errorCount + 1
								errors.append(family)

		file = open(self.output,'w')
		for key in toWrite:
			file.write(toWrite[key])
		file.close()

		print("Errors: ")
		print(errorCount)
		print(errors)

	def writeNewFilePlnTFDB(self):
		errorCount = 0
		errors = []
		toWrite = {}
		for family_file in self.gene_family_file:
			header = True
			with open(family_file) as allFile:
				for line in allFile:
					if header:
						header = False

					else:
						line = line.strip()
						cols = line.split("\t")
						family = cols[2]
						if family in self.plnAnnotations:
							toWrite[family] = family + "\t\t" + self.plnAnnotations[family] + "\n"
						else:
							errorCount = errorCount + 1
							errors.append(family)

		file = open(self.output,'w')
		for key in toWrite:
			file.write(toWrite[key])
		file.close()

		print("Errors: ")
		print(errorCount)
		print(errors)

def getArguments():
	"""Get the arguments of the command executed
	
	Get the arguments and their values of the program execution
	
	Returns:
		Object -- The arguments with values
	"""
	parser = argparse.ArgumentParser()
	parser.add_argument("-t", "--type", help = "eggnog or plntfdb.")
	parser.add_argument("-a", "--annotations", help = "Annotations folder for eggnog analysis.")
	parser.add_argument("-p", "--annotations_plntfdb", help = "Annotations folder for plntfdb analysis.")
	parser.add_argument("-f", "--gene_family_file", help = "The with the relation between the genes and gene families - separate by [,].")
	parser.add_argument("-o", "--output", help = "The result file.")

	data   = parser.parse_args()

	if isEmpty(data.type) or (data.type != 'eggnog' and data.type != 'plntfdb'):
		print("Is required the type be eggnog or plntfdb.")
		parser.print_help()
		exit()

	if isEmpty(data.gene_family_file):
		print("Is required the gene family file.")
		parser.print_help()
		exit()

	if isEmpty(data.output):
		print("Is required the result file.")
		parser.print_help()
		exit()

	if data.type == 'eggnog':
		if isEmpty(data.annotations):
			print("Is required the annotations in eggnog mode.")
			parser.print_help()
			exit()

		if not data.annotations.endswith("/"):
			data.annotations = data.annotations + "/"

	if data.type == 'plntfdb':
		if isEmpty(data.annotations_plntfdb):
			print("Is required the annotations in plntfdb mode.")
			parser.print_help()
			exit()

	return data

'''
Execute a command in the linux terminal
@Param	{String}	command	Command to execute
@return	{String}			Result of the execution
'''
def cmdline(command):
    process = subprocess.Popen(
        args  = command,
        shell = True
    )

    return process.communicate()[0]

def isEmpty(var):
	"""Verify if a variable is empty
	
	Verify if a variable is empty
	
	Arguments:
		var {all} -- Variable
	
	Returns:
		[boolean] -- If the variable is 'None' or '' or the length of it is 0
	"""
	return var == None or var == '' or len(var) == 0

arguments = getArguments()
GeneFamilyAnnotations = geneFamilyAnnotations(arguments.type, arguments.annotations, arguments.gene_family_file, arguments.annotations_plntfdb, arguments.output)
if GeneFamilyAnnotations.verifyType("eggnog"):
	GeneFamilyAnnotations.getAnnotationsEggnog()
	GeneFamilyAnnotations.writeNewFileEgggnog()

else:
	GeneFamilyAnnotations.getAnnotationsPlnTFBD()
	GeneFamilyAnnotations.writeNewFilePlnTFDB()