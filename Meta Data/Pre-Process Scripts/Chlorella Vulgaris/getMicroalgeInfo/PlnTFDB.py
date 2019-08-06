from library import *

class PlnTFDB(object):
	"""docstring for PlnTFDB"""
	def __init__(self, arg, folder):
		self.to_execute     = arg.PlnTFDB
		self.fasta_proteins = arg.fasta_proteins
		self.cpu            = arg.cpu
		self.hmm_file       = arg.hmm_file
		self.rules_file     = arg.rules_file
		self.folder_name    = arg.folder_name + '/PlnTFDB'
		self.folder         = folder
		if self.to_execute:
				self.out_file       = self.folder_name + '/' + self.fasta_proteins + '.hmmsearch.out'
				self.tblout_file    = self.folder_name + '/' + self.fasta_proteins + '.hmmsearch.tblout'
				self.domtblout_file = self.folder_name + '/' + self.fasta_proteins + '.hmmsearch.domtblout'
				self.result_file    = self.folder_name + '/' + self.fasta_proteins + '.hmmsearch.families'

	def execute(self):
		if self.to_execute:
			createFolder(self.folder_name)
			self.executeHmmsearch()
			self.executePerl()
			self.addPlnTFDB()

	def executeHmmsearch(self):
		command = 'hmmsearch --cpu ' + self.cpu + ' --cut_ga -o ' + self.out_file + ' --tblout ' + self.tblout_file + ' --domtblout ' + self.domtblout_file + ' ' + self.hmm_file + ' ' + self.fasta_proteins
		cmdline(command)

	def executePerl(self):
		command = 'perl ' + self.folder + 'assign_family_membership.pl -pfam ' + self.domtblout_file + ' -o ' + self.result_file + ' -r ' + self.rules_file
		cmdline(command)

	def addPlnTFDB(self):
		command = "sed -i -e 's/\\t/\\tPlnTFDB\\t/g' " + self.result_file
		cmdline(command)