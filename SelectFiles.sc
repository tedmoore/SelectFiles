SelectFiles {

	*new {
		arg input, selectExtensions, numChannels, recursive = true, verbose = false;

		selectExtensions = selectExtensions ? ['wav','aif','aiff'];

		numChannels = numChannels ? [1,2];

		if(input.isKindOf(String),{
			// a string was passed
			var pn = PathName(input);
			if(pn.isFile,{
				^this.cleanAndStrip([pn],selectExtensions,numChannels,verbose);
			},{
				// it's not a file
				if(pn.isFolder,{
					// it is a folder
					var list = this.consumeFolder(pn,recursive);
					^this.cleanAndStrip(list,selectExtensions,numChannels,verbose);
				},{
					// it's not a file or a folder;
					"% thing passed is neither file or folder...".format(thisMethod).warn;
				});
			});
		},{
			if(input.isKindOf(Array),{
				// it's an array;
				var list = List.new;
				input.do({
					arg in;
					var pn = PathName(in);
					if(pn.isFile,{
						// it is a file
						list.add(pn);
					},{
						// it's not a file
						if(pn.isFolder,{
							// it is a folder
							list.addAll(this.consumeFolder(pn,recursive));
						},{
							"% item in input array is neither a folder nor a file...".format(thisMethod).warn;
						});
					});
				});
				^this.cleanAndStrip(list,selectExtensions,numChannels,verbose);
			},{
				"% thing passed is neither string nor array...".format(thisMethod).warn;
			});
		});
	}

	*cleanAndStrip {
		arg pns, selectExt,numChannels, verbose = false;
		var returns = this.selectExt(pns,selectExt,numChannels,verbose);
		^this.stripPathNames(returns);
	}

	*consumeFolder {
		arg pn, recursive;
		var returnList = List.new;
		returnList.addAll(pn.files);

		if(recursive,{
			pn.folders.do({
				arg folder;
				returnList.addAll(this.consumeFolder(folder,true));
			});
		});
		^returnList;
	}

	*selectExt {
		arg pns, exts,numChannels, verbose = false;
		if(exts.notNil,{
			^pns.select({
				arg pn;
				var ext = pn.extension;
				var nChan = SoundFile.use(pn.fullPath,{arg sf; sf.numChannels});
				if(verbose,{"---Checking: %".format(pn.fullPath).postln;});
				exts.includes(ext.asSymbol).and(numChannels.includes(nChan));
			});
		},{
			^pns;
		});
	}

	*stripPathNames {
		arg pns;
		^pns.collect({
			arg pn;
			pn.fullPath;
		});
	}
}