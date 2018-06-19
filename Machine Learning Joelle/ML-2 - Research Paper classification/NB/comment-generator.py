import sys,os.path

class CommentMaker():
    def __init__(self,filename):
        self.codes={}
        self.template=""
        self.make_comments(filename)
        self.show_unused()

    def make_comments(self,filename):
        with open(filename,"r") as f:
            lines=f.read().split("\n")
        self.columns=lines[0].split("\t")
        lines=[line for line in lines[1:] if line.strip()]

        with open("all-comments.txt","w") as f:
            for line in lines:
                parsed=self.parse_line(line)
                f.write(parsed["name"].upper())
                f.write("\n"*5)
                f.write(self.get_comment(parsed))
                f.write("\n"*10)

    def get_intro(self,parsed):
        score=float(parsed["q1"])+float(parsed["q2"])+float(parsed["q3"])
        total="%s/100"%score
        if score==100:
            return "It's very easy grading a perfect assignment! "+total
        if score>90:
            return "Great work. "+total
        if score>70:
            return "Good work. "+total
        if score>40:
            return "Well, good effort! "+total
        return "I graded your assignment. "+total

    def get_conclusion(self,parsed):
        if not parsed["comment codes"]:
            return ""

        lines=[self.get_comment_from_code(code) for code in parsed["comment codes"]]
        return "\n\n".join(lines)

    def show_unused(self):
        for column in self.columns:
            if column not in self.used_columns and column not in ("","comment codes"):
                print("'%s' was unused in comment template."%column)

    def get_comment_from_code(self,code):
        if code==' ':
            return ""
        if self.codes:
            return self.codes[code]

        with open("comment-codes.txt","r") as f:
            data=f.read()
        for line in data.split("\n"):
            if not line.strip() or line[0]=="#" or line[1]!="=":
                continue
            self.codes[line[0]]=line[2:]

        return self.codes[code]

    def get_comment(self,parsed):
        if not self.template:
            with open("comment-template.txt","r") as f:
                self.template=f.read()

        self.used_columns=set()
        comment=self.template
        for column in parsed:
            key="[%s]"%column
            if key in self.template:
                self.used_columns.add(column)
            comment=comment.replace(key,parsed[column])

        comment= "%s\n\n\n%s\n\n\n%s"%(self.get_intro(parsed),comment,self.get_conclusion(parsed))
        while "\n\n\n" in comment:
            comment=comment.replace("\n\n\n","\n\n")
        return comment

    def parse_line(self,line):
        split=line.split("\t")
        data={"name":split[0]}
        for i,column in enumerate(self.columns):
            if not column.strip():
                continue
            if i==0:
                column="name"
            data[column]=split[i]
        return data

if len(sys.argv)>=1 and os.path.isfile(sys.argv[1]):
    cm=CommentMaker(sys.argv[1])
else:
    print("Abort. Bad arguments: 'python3 comment-generator.py student-grades2.tsv'")
