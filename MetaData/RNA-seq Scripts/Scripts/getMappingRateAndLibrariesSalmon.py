import sys, getopt, os, argparse, re

class salmonMappingLibraries:
	"""docstring for salmonMappingLibraries"""
	def __init__(self):
		self.home_directory        = '/home/rdorado/Chlamy/'
		self.foldersToIgnore       = ['Scripts', 'expression_values', 'references']
		self.experiments           = []
		self.actual_eperiment      = ''
		self.actual_condition      = ''
		self.actual_eperiment_file = ''
		self.actual_condition_file = ''
		self.file_name             = 'salmon_quant.log'
		self.search1               = 'Mapping rate'
		self.search2               = "Automatically detected most likely library type as"
		self.actual_mapping_rate   = ''
		self.actual_library        = ''
		self.result_file_name      = ''

	def setResultFileName(self, filename):
		self.result_file_name = filename

	def getAll(self):
		list_of_folders = self.ll(self.home_directory)
		experiments     = []
		for folder in list_of_folders:
			isExperiment = True
			for folderIgn in self.foldersToIgnore:
				if folder == folderIgn:
					isExperiment = False
					break

			if isExperiment:
				experiments.append(folder)

		self.setExperiments(experiments)

	def setChoosed(self, experiments):
		self.setExperiments(experiments.split(','))

	def setExperiments(self, experiments):
		self.experiments = experiments

	def setActualDirectory(self, experiment):
		self.actual_eperiment_file = self.home_directory + experiment + '/Salmon/'
		self.actual_eperiment      = experiment

	def getConditions(self):
		return self.ll(self.actual_eperiment_file)

	def setActualCondition(self, condition):
		self.actual_condition_file = self.actual_eperiment_file + condition + '/logs/'
		self.actual_condition      = condition

	def processAConditions(self, condition):
		self.setActualCondition(condition)
		self.readFile()
		self.writeFile()

	def writeFile(self):
		file = open(self.home_directory + 'Scripts/' + self.result_file_name, "a")
		line = self.actual_eperiment + ',' + self.actual_condition + ',"' + self.actual_mapping_rate + '",' + self.actual_library + "\n"
		file.write(line)
		file.close()

	def readFile(self):
		mappingRate              = self.searchInFile(self.search1)
		library                  = self.searchInFile(self.search2)
		if mappingRate:
			self.actual_mapping_rate = self.getInfo(mappingRate)
		else:
			self.actual_mapping_rate = ''
		if library:
			self.actual_library = self.getInfo(library)
		else:
			self.actual_library = ''

	def getInfo(self, info):
		allLine = info.split(' ')
		return allLine[len(allLine) - 1].replace("\n", '')
	
	def searchInFile(self, search):
		with open(self.actual_condition_file + self.file_name) as origin_file:
			for line in origin_file:
				result = re.findall(search, line)
				if result:
					return line
		

	def processAExperiment(self, experiment):
		self.setActualDirectory(experiment)
		conditions = self.getConditions()
		for condition in conditions:
			self.processAConditions(condition)

	def processExperiments(self):
		for experiment in self.experiments:
			self.processAExperiment(experiment)

	'''
	Get the list of directories and files of a directory
	@Param	{String}	directory	The directory to get all the files and directories
	@return	{array}					List of directories and files
	'''
	def ll(self, directory):
	    return os.listdir(directory)



def getMappingRateandLibariesSalmon(all, experiments, filename):
	salmonMappingLibrarie = salmonMappingLibraries()
	salmonMappingLibrarie.setResultFileName(filename)
	if all:
		salmonMappingLibrarie.getAll()
	else:
		salmonMappingLibrarie.setChoosed(experiments)

	salmonMappingLibrarie.processExperiments()





def getArguments():
	parser = argparse.ArgumentParser()
	parser.add_argument("-f", "--filename", help = "The name of the file to write the result")
	parser.add_argument("-e", "--experiments", help = "The name of the experiments, separeted by ','")
	parser.add_argument("-a", "--all", help = "Set it to run all the experiments. Do not set options -e y -a together", action="store_true")
	data = parser.parse_args()
	if (isEmpty(data.experiments) and data.all == False) or (isEmpty(data.experiments) == False and data.all == True) or isEmpty(data.filename):
		print "Is required one option and only one option to be setted"
		parser.print_help()
		exit()
	return data

def isEmpty(var, con = True):
	return var == None or var == '' or len(var) == 0

arguments = getArguments()
getMappingRateandLibariesSalmon(arguments.all, arguments.experiments, arguments.filename)