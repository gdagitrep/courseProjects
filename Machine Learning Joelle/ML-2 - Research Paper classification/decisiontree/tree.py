import utility 

class Tree:
    name = 1
    def __init__(self):
        self.right = None
        self.left = None
        self.node = None
        self.probdict = utility.initCatDict()
        self.id =Tree.name
        Tree.name=Tree.name+1
    
    def setRightChild(self,value):
        self.right = value

    def setLeftChild(self,value):
        self.left=value

    def setData(self,value):
        self.node = value

    def setProbDict(self, probdict):
        self.probdict = probdict

    def getProbDict(self):
        return self.probdict

    def getData(self):
        return self.node

    def getTree(self):
        return (self.node, self.right, self.left)

    def nodeList(self):
        result=[]
        result.append(self)
        if self.right != None:
            result.extend(self.right.nodeList())
        if self.left !=None:       
            result.extend(self.left.nodeList())
        return result

    def getId(self):
        return self.id

    def isLeaf(self):
        return self.right == None and self.left==None

    def prune(self, nodeRemoveId):
        if self.id == nodeRemoveId:
            return None
        tree = Tree()
        tree.setData(self.node)
        tree.setProbDict(self.probdict)
        tree.setRightChild((self.right.prune(nodeRemoveId) if self.right != None and not isinstance(self.right, dict) else None))
        tree.setLeftChild((self.right.prune(nodeRemoveId) if self.right != None and not isinstance(self.left, dict)  else None))
        return tree    

    def __str__(self):
        if self.right == None and self.left == None:
            return "( " +str(self.node) + " )"
        return "( "+str(self.node)+", " + str(self.right) +", "+str(self.left)+")"
