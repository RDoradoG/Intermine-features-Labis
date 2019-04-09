def isEmpty(var):
	"""Verify if a variable is empty
	
	Verify if a variable is empty
	
	Arguments:
		var {all} -- Variable
	
	Returns:
		[boolean] -- If the variable is 'None' or '' or the length of it is 0
	"""
	return var == None or var == '' or len(var) == 0