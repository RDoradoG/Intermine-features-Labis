class bioEntity():
	"""docstring for bioEntity"""
	def __init__(self):
		self.Chromosome = ''
		self.Source     = ''
		self.Type       = ''
		self.Init       = ''
		self.End        = ''
		self.Score      = '.'
		self.Strand     = ''
		self.ScoreCDS   = '.'
		self.ID         = ''
		self.Name       = ''
		self.Parent     = ''
		self.Extras     = ''
		self.Gene     	= ''

	def get_line(self):
		base = self.Chromosome + "\t" + self.Source + "\t" + self.Type + "\t" + str(self.Init) + "\t" + str(self.End) + "\t" + self.Score + "\t" + self.Strand + "\t" + self.ScoreCDS + "\tID=" + self.ID
		if self.Name != '':
			base = base + ";Name=" + self.Name

		if self.Parent != '':
			base = base + ";Parent=" + self.Parent

		if self.Gene != '':
			base = base + ";gene=" + self.Gene

		if self.Extras != '':
			base = base + ";" + self.Extras	
			
		return base

	def set_Chromosome(self, Chromosome):
		self.Chromosome = Chromosome

	def get_Chromosome(self):
		return self.Chromosome

	def set_Source(self, Source):
		self.Source = Source

	def get_Source(self):
		return self.Source

	def set_Type(self, Type):
		self.Type = Type

	def get_Type(self):
		return self.Type

	def set_Init(self, Init):
		self.Init = Init

	def get_Init(self):
		return self.Init

	def set_End(self, End):
		self.End = End

	def get_End(self):
		return self.End

	def set_Gene(self, Gene):
		self.Gene = Gene

	def get_Gene(self):
		return self.Gene

	def set_Score(self, Score):
		self.Score = Score

	def get_Score(self):
		return self.Score

	def set_Strand(self, Strand):
		self.Strand = Strand

	def get_Strand(self):
		return self.Strand

	def set_ScoreCDS(self, ScoreCDS):
		self.ScoreCDS = ScoreCDS

	def get_ScoreCDS(self):
		return self.ScoreCDS

	def set_ID(self, ID):
		self.ID = ID

	def get_ID(self):
		return self.ID

	def set_Name(self, Name):
		self.Name = Name

	def get_Name(self):
		return self.Name

	def set_Parent(self, Parent):
		self.Parent = Parent

	def get_Parent(self):
		return self.Parent

	def set_Extras(self, Extras):
		self.Extras = Extras

	def get_Extras(self):
		return self.Extras