SOURCEDIR = .
SOURCEFILES := $(shell find $(SOURCEDIR) -name '*.java')
CLASSFILES = $(SOURCEFILES:.java=.class)	 

all: $(CLASSFILES)

$(CLASSFILES) : $(SOURCEFILES)
	javac  -encoding ISO-8859-1 $(SOURCEFILES)

clean: 
	rm -rf $(CLASSFILES) 
