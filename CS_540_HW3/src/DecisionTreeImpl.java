import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
 * 
 * You must add code for the 5 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	private DecTreeNode root;
	private List<String> labels; // ordered list of class labels
	private List<String> attributes; // ordered list of attributes
	private List<Instance> instances;
	private Map<String, List<String>> attributeValues; // map to ordered
														// discrete values taken
														// by attributes
	int temp;
	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary
		// this is void purposefully
	}

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train: the training set
	 */
	DecisionTreeImpl(DataSet train) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		this.instances = train.instances;
		ArrayList<String> attributesLeft = new ArrayList<String>();

		root = buildTree(this.instances, attributesLeft, null, null);
	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning
	 * set.
	 * 
	 * @param train: the training set
	 * @param tune: the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		// TODO: add code here
	}

	@Override
	public String classify(Instance instance) {

		// TODO: add code here
		return null;
	}

	@Override
	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {

		printTreeNode(root, null, 0);
	}
	
	/**
	 * Prints the subtree of the node
	 * with each line prefixed by 4 * k spaces.
	 */
	public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		String value;
		if (parent == null) {
			value = "ROOT";
		} else{
			String parentAttribute = attributes.get(parent.attribute);
			List<String> debug = attributeValues.get(parentAttribute);
			value = debug.get(p.parentAttributeValue);
		}
		sb.append(value);
		if (p.terminal) {
			sb.append(" (" + labels.get(p.label) + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + attributes.get(p.attribute) + "?}");
			System.out.println(sb.toString());
			for(DecTreeNode child: p.children) {
				printTreeNode(child, p, k+1);
			}
		}
	}

	@Override
	public void rootInfoGain(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		int numOfAttributes;
		double conditionalEntropy, entropy, mutualInfo;
		entropy = calculateEntropy(train.instances, train.labels.size());
		mutualInfo = 0.0;
		
		for (int i = 0; i < train.attributes.size(); i++) {
			numOfAttributes = train.attributeValues.get(train.attributes.get(i)).size();
			conditionalEntropy = calculateConditionalEntropy(i, train.instances, numOfAttributes, train.labels.size());
			
			mutualInfo = entropy - conditionalEntropy;
			System.out.printf(train.attributes.get(i) + " %.5f\n", mutualInfo);
		}
		
	}
	
	private DecTreeNode buildTree(List<Instance> instances, List<String> attrRemaining, List<Instance> parentInstances, DecTreeNode parent){
		int winningLabel;
		DecTreeNode curr;
		List<String> newListOfAttrs;
		temp++;
		System.out.println("times calling build tree = " + temp);
		/*If we are out of training examples, return the overall majority vote*/
	    if(instances.isEmpty()){
	    	winningLabel = getMajorityVote(parentInstances);
	        return new DecTreeNode(winningLabel, null, parent.attribute, true);
	    } 
	    /*If all instances have the same label, then the majority vote is that label*/
	    else if(allInstancesHaveSameLabel(instances)){
	        /*Since all remaining instances have same label, pick one*/
	    	winningLabel = instances.get(0).label;
	        return new DecTreeNode(winningLabel, null, parent.attribute, true);
	    } 
	    /*If we are out of attributes, return majority vote in instances*/
	    else if(attrRemaining.isEmpty()){
	    	winningLabel = getMajorityVote(instances);
	        return new DecTreeNode(winningLabel, null, parent.attribute, true);
	    } 
	    else {
	        curr = highestInformationGain(instances, attrRemaining, parent);
	        
	        /*Create a new list of attributes for the next recursive call*/
	        newListOfAttrs = new ArrayList<String>();
	        for(int j = 0; j < attrRemaining.size(); j++){
	        	if(j != curr.attribute){
	        		newListOfAttrs.add(Integer.toString(j));
	        	}
	        }
	        //for(int k = 0; k < attrRemaining.size(); k++){
	        for(int k = 0; k < attributeValues.get(attributes.get(curr.attribute)).size(); k++){
	            List<Instance> examples = new ArrayList<Instance>();
	            for(Instance item : instances){
	            	if(item.attributes.get(curr.attribute) == k)
	            		examples.add(item);
	            }
	            DecTreeNode subtree = buildTree(examples, newListOfAttrs, instances, curr);
	            curr.children.add(subtree);
	        }
	        return curr;
	    }
	}
	
	private DecTreeNode highestInformationGain(List<Instance> inst, List<String> attrRemaining, DecTreeNode parent) {
	    double entropy = -1, conditionalEntropy = -1;
	    int highestEntropy = -1;
	    double highestInfoGain = -1, infoGain = -1;

	    //calculate entropy
	    entropy = calculateEntropy(inst, labels.size());
	    
	    for(int i = 0; i < attrRemaining.size(); i++){
	    	//conditionalEntropy = calculateConditionalEntropy(i, inst, attrRemaining.size(), labels.size());
	    	conditionalEntropy = calculateConditionalEntropy(i, inst, attributeValues.size(), labels.size());
	    	
	    	infoGain = entropy - conditionalEntropy;
	    	if(infoGain > highestInfoGain){
	    		highestInfoGain = infoGain;
	    		highestEntropy = i;
	    	}
	    }
	    if(parent == null){
	    	return new DecTreeNode(getMajorityVote(inst), highestEntropy, -1, (false || (attrRemaining.size() == 1)));
	    }
	    else{
	    	return new DecTreeNode(getMajorityVote(inst), highestEntropy, parent.attribute, false);
	    }
	}
	
	private double calculateConditionalEntropy(int attributeId, List<Instance> instances, int numberOfAttributes, int numberOfLabels){
		int totalInstances = instances.size();
		int index;
		int[] count = new int[numberOfAttributes];
		double conditionalEntropy = 0;
		double countOfAttr, prob;
		double entropyBasedOnConditional;
		double instanceSize = (double) instances.size();
		ArrayList<ArrayList<Instance>> copyOfLabel = new ArrayList<ArrayList<Instance>>();
		
		//Create new empty lists
		for (int i = 0; i < numberOfAttributes; i++) {
			copyOfLabel.add(new ArrayList<Instance>());
		}
		/*Count the number of attributes*/
		if(temp == 12){}
		for (Instance item : instances) {
			index = item.attributes.get(attributeId);
			count[index]++;
			copyOfLabel.get(index).add(item);
		}
		/*for each label, calculate the probability and add it to the total entropy*/
		for (int i = 0; i < count.length; i++) {
			if (totalInstances != 0 && count[i] != 0) {
				countOfAttr = (double) count[i];
				prob = countOfAttr/instanceSize; 
				//calculate the entropy for the given attribute
				entropyBasedOnConditional = calculateEntropy(copyOfLabel.get(i), numberOfLabels);
				conditionalEntropy += prob * entropyBasedOnConditional;
			}
		}
		return conditionalEntropy;
	}
	
	private double calculateEntropy(List<Instance> instances, int numberOfLabels){
		double entropy = 0, prob = 0;
		int[] count = new int[numberOfLabels];
		final double log2 = Math.log10(2);
		double countOfAttr;
		double instanceSize = (double) instances.size();
		
		//get the number of label occurrences in the instances
		for (Instance instance : instances) {
			count[instance.label]++;
		}
		
		/*for each label, calculate the probability and add it to the total entropy*/
		for (int i = 0; i < count.length; i++) {
			if (count[i] != 0 && !(instances.isEmpty())) {
				countOfAttr = (double) count[i];
				prob = countOfAttr / instanceSize;
				entropy += prob * Math.log10(prob) / log2;
			}
		}
		entropy = entropy * -1;
		return entropy;
	}
	
	private Integer getMajorityVote(List<Instance> instances){
		//highestVote store the greatest number of votes. winner stores the 
		//associated label with the greatest number of votes
		int highestVotes = -1, winner = -1;
		//count will hold the total number of instances with a given label
	    int[] count = new int[labels.size()];
	    
	    //count the labels for each instance
	    for(Instance instance : instances){
	        count[instance.label]++;
	    }
	    
	    //find which label had the most votes
	    for(int i = 0; i < labels.size(); i++){
	        if(count[i] > highestVotes){
	            highestVotes = count[i];
	            winner = i;
	        }
	    }
	    return winner;
	}
	
	private boolean allInstancesHaveSameLabel(List<Instance> instances){
		int prevLabel = -1;//label will not be negative
		for(Instance instance : instances){
			if(prevLabel == -1){
				prevLabel = instance.label;
			}
			else if(instance.label != prevLabel){
				return false;
			}
		}
		return true;
	}
}
