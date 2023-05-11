


import argparse

parser = argparse.ArgumentParser()
parser.add_argument('-n','--name', action="append")
parser.add_argument('-d','--date', action="append")
args = parser.parse_args()
print(args)
print(type(args))
name_ = args.name
date_ = args.date

print(type(name_))
print(type(date_))
print('the people %s said that the date is %s'%(name_, date_))