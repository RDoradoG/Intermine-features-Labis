#sudo apt-get install python-requests
#sudo apt-get install python-beautifulsoup
#import sys, getopt, urllib2, requests, json, csv, shlex, subprocess
#from BeautifulSoup import BeautifulSoup

import sys, getopt, urllib2, subprocess, os, re
import xml.etree.ElementTree as ET

'''
	Set default values
'''
homeDirectory      = '/home/rdorado/Chlamy/'
salmonDirectory    = '/Salmon'
searchURL          = 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=SRA&term='
summaryUrl         = 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=SRA&id='
excludeDirectories = ['Scripts', 'expression_values', 'references']

'''
Print help and exit of the script
'''
def printHelp():
	print 'Help\n     -a, --all\t\t\t\tGet meta data from all the experiments.\n     -e, --experiment\t{experiment}\tGet meta data from an experiment.\n     -f, --file\t\t{file name}\tThe file name to print the result.'
	sys.exit()

'''
Get the list of directories and files of a directory
@Param	{String}	directory	The directory to get all the files and directories
@return	{array}					List of directories and files
'''
def ll(directory):
    return os.listdir(directory)

'''
Get the SSR id of a experiment with the directory names of the Salmon
@Param	{array}	filesName	List of directories
@return	{array}				List of SRR ids
'''
def ssrOfFilesName(filesName):
	files = []
	for filename in filesName:
		file              = {}
		file['SRR']       = filename.split('_')[0]
		file['Condition'] = filename.replace(file['SRR'] + '_', '').replace('-', '.')
		files.append(file)
	return files

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

'''
Get all the experiments names
@return	{array}	All the experiments names
'''
def getAllExperiments():
	allExperiments = []
	for directory in ll(homeDirectory):
		if not directory in excludeDirectories:
			allExperiments.append(directory)
	return allExperiments

'''
Get the SSR id of an expeiment
@Param	{String}	experiment	Name of the experiment
@return	{array}					All the SSR id of an experiment
'''
def getSrrOFExperiment(experiment):
	return ssrOfFilesName(ll(homeDirectory + experiment + salmonDirectory))

'''
Set the result of the query to the metadata for an experiment
@Param	{array}	srrArray	The list of SSR of the experiment
@return	{array}				All the meta data
'''
def setSrrMetaData(srrArray):
	result = []
	for srr in srrArray:
		row              = {}
		condition = re.match('^[0-9]', srr['Condition'])
		if condition:
			row['Condition'] = 'X' + srr['Condition']
		else:
			row['Condition'] = srr['Condition']
		row['metaData']  = getEsummary(srr['SRR'])
		result.append(row)
	return result

'''
The main function
@Param	{array}	ssr	SSR Id.
@return	{array}		All the meta data of the SSR Id.
'''
def getEsummary(ssr):
	esearchUrl    = searchURL + ssr #set the url
	esearchResult = urllib2.urlopen(esearchUrl).read()
	esearchXml    = ET.fromstring(esearchResult)
	esearchIds    = []
	for esearchTag in esearchXml: #Loop the answer
		if esearchTag.tag == 'IdList': #Find the tag 'IdLust'
			for esearchId in esearchTag: #Loop al the ids
				thisRow          = {}
				thisRow['ssrId'] = esearchId.text
				esummaryUrl      = summaryUrl + esearchId.text # Set the url for the meta data
				esummaryResult   = urllib2.urlopen(esummaryUrl).read()
				esummaryXml      = ET.fromstring(esummaryResult)
				for esummaryTag in esummaryXml: #Loop the answer
					if esummaryTag.tag == 'DocSum': #Find the tag 'DocSum'
						for esummaryDocSumTag in esummaryTag: #Loop the childs
							if esummaryDocSumTag.tag == 'Item': #Find the tag 'Item' with propertie name = ExpXml
								if esummaryDocSumTag.attrib['Name'] == 'ExpXml':
									xmlString = '<root>' + esummaryDocSumTag.text.encode('utf8') + '</root>' #Add a root tag to the encoded xml
									resultXml = ET.fromstring(xmlString)
									result    = {}
									for rootTag in resultXml: #Loop the answer
										if rootTag.tag == 'Summary': #Find the tag 'Summary'
											for childTag in rootTag: #Loop the childs
												if childTag.tag == 'Title': #Find the tag 'Title'
													result['Title'] = childTag.text
												if childTag.tag == 'Platform': #Find the tag 'Platform'
													result['Instrument']                     = {}
													result['Instrument']['name']             = childTag.text
													result['Instrument']['instrument_model'] = childTag.attrib['instrument_model']
										if rootTag.tag == 'Experiment':  #Find the tag 'Experiment'
											result['Experiment'] = rootTag.attrib['name']
										if rootTag.tag == 'Library_descriptor': #Find the tag 'Library_descriptor'
											result['Library_descriptor'] = {}
											for childTag in rootTag: #Loop the childs
												result['Library_descriptor'][childTag.tag]               = {}
												result['Library_descriptor'][childTag.tag]['properties'] = []
												for grandChildTag in childTag: #Loop the grandChilds
													result['Library_descriptor'][childTag.tag]['properties'].append(grandChildTag.tag)
												result['Library_descriptor'][childTag.tag]['value'] = childTag.text
				thisRow['data'] = result
				esearchIds.append(thisRow)
	return esearchIds

def buildFile(data, experiment, filename):
	fileToWrite = filename + "-" + experiment + ".txt"
	printMessage("Create and write file: " + fileToWrite)
	file        = open(fileToWrite, "w")
	titles      = "experiment\tName\tDescription\tinstrument\tstrategy\tsource\tselection\tlayout" #\tconstructionProtocol"
	file.write(titles + "\n")
	for info in data:
		row      = info['metaData'][0]['data']
		rowText  = experiment
		rowText  = rowText + "\t" + info['Condition']
		rowText  = rowText + "\t" + row['Title']
		rowText  = rowText + "\t" + row['Instrument']['instrument_model']
		rowText  = rowText + "\t" + row['Library_descriptor']['LIBRARY_STRATEGY']['value']
		rowText  = rowText + "\t" + row['Library_descriptor']['LIBRARY_SOURCE']['value']
		rowText  = rowText + "\t" + row['Library_descriptor']['LIBRARY_SELECTION']['value']
		rowText  = rowText + "\t" + row['Library_descriptor']['LIBRARY_LAYOUT']['properties'][0]
		#rowText = rowText + "\t" + row['Library_descriptor']['LIBRARY_CONSTRUCTION_PROTOCOL']
		file.write(rowText.encode('utf8') + "\n")
	file.close()

def printMessage(message):
	print message + "\n"

def runPipeline(experiment, filename):
	SrrOFExperiment = getSrrOFExperiment(experiment)
	SrrMetaData     = setSrrMetaData(SrrOFExperiment)
	buildFile(SrrMetaData, experiment, filename)
	printMessage("--------------------------------")

'''
The main function
@Param	{array}	argv	List arguments.
@return	{null}
'''
def main(argv):
	experiment = ''
	filename   = ''
	all        = False
	try:
		opts, args = getopt.getopt(argv, "hae:f:", ["help", "all", "experiment=", "file="])
	except getopt.GetoptError:
		printHelp()
	for opt, arg in opts:
		if opt in ("-h", "--help"):
			printHelp()
		if opt in ("-e", "--experiment"):
			experiment = arg.replace('/', '')
		if opt in ("-a", "--all"):
			all = True
		if opt in ("-f", "--file"):
			filename = arg
	if filename == '':
		printHelp()
	if all == False and experiment == '':
		printHelp()
	allResults = []
	printMessage("-- Starting --")
	if all:
		printMessage("\t-- All --")
		allExperiments       = getAllExperiments()
		allExperimentsLength = len(allExperiments)
		counter              = 0;
		for experiment in allExperiments:
			counter    += 1 
			experiment = experiment.replace('/', '')
			printMessage("Experiment: " + experiment + " --> " + str(counter) + " of " + str(allExperimentsLength))
			runPipeline(experiment, filename)
	else:
		printMessage("\t-- A experiment --")
		runPipeline(experiment, filename)

main(sys.argv[1:])