import sys, getopt, os, argparse


def getDescEggOg(inpuFile):
	outfile = inpuFile + ".result"
	newlines = []
	with open(inpuFile) as file:
		for line in file:
			line = takeOfEnter(line)
			columns = line.split("\t")
			eggnogs = columns[0].split(",")
			for eggnog in eggnogs:
				obj = {
					'eggnog': eggnog,
					'description': columns[1]
				}
				save = True
				for newline in newlines:
					if newline['eggnog'] == eggnog:
						save = False
						if newline['description'] != columns[1]:
							print(eggnog + "has two descriptions: " + newline['description'] + "   !=   " + columns[1])
						break

				if save:
					newlines.append(obj)
	file = open(outfile, "a")
	for newline in newlines:
		file.write(newline['eggnog'] + "\t" + newline['description'] + "\n")
	file.close()

def takeOfEnter(var):
	return var.replace("\n", "")

def getArguments():
	"""Get the arguments of the command executed
	
	Get the arguments and their values of the program execution
	
	Returns:
		Object -- The arguments with values
	"""
	parser = argparse.ArgumentParser()
	parser.add_argument("-i", "--input-file", help = "The name of the eggnog file")
	data   = parser.parse_args()
	if isEmpty(data.input_file):
		print("Is required a fasta file")
		parser.print_help()
		exit()
	return data

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
getDescEggOg(arguments.input_file)