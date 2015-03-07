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
		// TODO: add code here
		buildTree(this.instances, this.attributes, null, -1);
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
			value = attributeValues.get(parentAttribute).get(p.parentAttributeValue);
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
		// TODO: add code here
	}
	
	private DecTreeNode buildTree(List<Instance> instances, List<String> attrRemaining, List<Instance> parentInst, Integer parentAtt){
		int winningLabel;
		DecTreeNode curr;
		int attrIndex;
		
		/*If we are out of training examples, return the overall majority vote*/
	    if(instances.isEmpty()){
	    	winningLabel = getMajorityVote(instances);
	        return new DecTreeNode(winningLabel, null, parentAtt, true);
	    } 
	    /*If all instances have the same label, then the majority vote is that label*/
	    else if(allInstancesHaveSameLabel(instances)){
	        /*Since all remaining instances have same label, pick one*/
	    	winningLabel = instances.get(0).label;
	        return new DecTreeNode(winningLabel, null, parentAtt, true);
	    } 
	    /*If we are out of attributes, return majority vote in instances*/
	    else if(attrRemaining.isEmpty()){
	    	winningLabel = getMajorityVote(instances);
	        return new DecTreeNode(winningLabel, null, parentAtt, true);
	    } 
	    else {
	        curr = highestInformationGain(instances, attrRemaining, parentAtt);
	        
	        
	        return curr;
	    }
	}
	
	private DecTreeNode highestInformationGain(List<Instance> inst, List<String> attrRemaining, Integer parentAtt) {
		// TODO Auto-generated method stub
		return null;
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
