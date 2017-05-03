import xml.etree.ElementTree as ET

def parse(inputFile, outFile):
    f = open(inputFile)
    fw=open(outFile,'a+')
    tree = ET.parse(f)
    sampleroot = tree.getroot()
    ts =""
    sampleData =""
    isReadException = False
    for sample in sampleroot:
        if sample.tag =="sample":
            if "ReadException:" in sample.attrib["rm"]:
                ts=sample.attrib["ts"]
                #print ts
                if sample[5].tag=="samplerData":
                    sampleData=sample[5].text
                    #print sampleData
                #print ts+", "+sampleData
                fw.write(ts+", "+sampleData +"\n")
     
        
if ( __name__ == "__main__"):

        parse("./AssertError.txt","./errorRequest.txt")
        
