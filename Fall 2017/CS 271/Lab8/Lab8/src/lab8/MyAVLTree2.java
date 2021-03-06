package lab8;
//AVLTree that handles duplicates
public class MyAVLTree2<T extends Comparable<T>>{
   	private static final int ALLOWED_IMBALANCE = 1;

	//Inner Class to capture a node
	private class MyNode<T extends Comparable<T>> {
		private T data;
		private MyNode<T> left;
		private MyNode<T> right;
		private int height=0;
		private int count=0; //Duplicates

		private MyNode(T e, MyNode<T> l, MyNode<T> r){
			data=e;
			left=l;
			right=r;
			count=1; //Duplicates
		}
	}

	//Data members
	private MyNode<T>root;
	private int size;

	//Operations
	public MyAVLTree2(){
		root = null;
		size=0;
	}

	public boolean contains(T element){
		return containsNode(root,element);
	}

	public boolean containsNode(MyNode<T> temp, T element){
		if (temp != null){
			int compared = temp.data.compareTo(element);
			if(compared==0)
				return true;
			else if(compared < 0)
				return containsNode(temp.right,element);
			else
				return containsNode(temp.left,element);
		}
		return false;
	}

	public void insert(T element){
		root = insertNode(root, element);
		size++;
	}

	private MyNode<T> insertNode(MyNode<T> temp, T element){
		if (temp==null)
			return new MyNode<T>(element,null,null);

		int compared = temp.data.compareTo(element);
		if(compared > 0)
			temp.left = insertNode(temp.left, element);
		else if (compared < 0)
			temp.right = insertNode(temp.right, element);
		else 	//For Duplicates, if equal, simply increment the count
			temp.count++;

		//call balance here
		return balance(temp);
	}

	public void remove(T element){
		root = remove(root,element);
	}

   private MyNode<T> remove(MyNode<T> temp, T element){
        if( temp == null )
            return temp;   // Item not found; do nothing

		int compared = temp.data.compareTo(element);
		if(compared > 0)  								//search left subtree
			temp.left = remove(temp.left,element);
		else if (compared < 0) 							//search right subtree
            temp.right = remove(temp.right,element);
        else if (compared == 0 && temp.count > 1) {//For Duplicates, delete one element only
        	temp.count--;
        	size--;
		}
        else if (temp.left!=null && temp.right!=null){ 	//Two children

			//find inorder successor: leftmost node in the right subtree
			MyNode<T> curr = temp.right;
			while(curr.left!=null)
				curr=curr.left;

			//write this value in place of temp:
			temp.data=curr.data;

			//and remove this new node:
			temp.right = remove(temp.right,temp.data);
		}
        else{											//One child or leaf:
            if (temp.left!=null)
            	temp=temp.left;							//left
            else
            	temp = temp.right;						//right or leaf
            size--;			//this code will always be called, even for two children
		}
        return balance(temp);
    }

	public int size(){
		return size;
	}

	private void inorder(MyNode<T> current){
		if(current==null)
			return;
		inorder(current.left);
		for (int i=0; i<current.count; i++)	//For Duplicates
			System.out.print(current.data+" ");
		inorder(current.right);
	}

	public void inorder(){
		System.out.print("Tree of size "+size+": ");
		inorder(root);
		System.out.println();
	}

	public void levelTraverse(){
		java.util.Queue<MyNode<T>> queue = new java.util.LinkedList<MyNode<T>>();
		MyNode<T> temp = root;
		System.out.print("Level Traversal: ");
		while(temp!=null){
			if (temp.count==1)
				System.out.print(temp.data+" ");
			else
				System.out.print(temp.data+"("+temp.count+") ");	//For Duplicates
			if (temp.left!=null)
				queue.offer(temp.left);
			if (temp.right!=null)
				queue.offer(temp.right);
			temp = queue.poll();
		}
		System.out.println();
	}

   private int height( MyNode<T> t ){
        if (t==null)
        	return -1;
        else
        	return t.height;
   }

    // Assume temp is either balanced or within one of being balanced
    private MyNode<T> balance(MyNode<T> temp){
        if(temp == null)
            return temp;

        if(height(temp.left) - height(temp.right) > ALLOWED_IMBALANCE){
            if(height(temp.left.left) >= height(temp.left.right))
                temp = rotateRight(temp);
            else
                temp = doubleLeftRight(temp);
		}
        else {
			if(height(temp.right) - height(temp.left) > ALLOWED_IMBALANCE){
				if(height(temp.right.right) >= height(temp.right.left))
					temp = rotateLeft(temp);
				else
					temp = doubleRightLeft(temp);
			}
		}
        temp.height = Math.max(height(temp.left), height(temp.right)) + 1;
        return temp;
    }

	//Fixes Left-Left case
	private MyNode<T> rotateRight( MyNode<T> k2 ){
	    MyNode<T> k1 = k2.left;
	    k2.left = k1.right;
	    k1.right = k2;
	    k2.height = Math.max( height( k2.left ), height( k2.right ) ) + 1;
	    k1.height = Math.max( height( k1.left ), k2.height ) + 1;
	    return k1;
	}

	//Fixes Right-Right case
	private MyNode<T> rotateLeft( MyNode<T> k1 ){
	    MyNode<T> k2 = k1.right;
	    k1.right = k2.left;
	    k2.left = k1;
	    k1.height = Math.max( height( k1.left ), height( k1.right ) ) + 1;
	    k2.height = Math.max( height( k2.right ), k1.height ) + 1;
	    return k2;
	}

	//Fixes Left-Right case
	private MyNode<T> doubleLeftRight( MyNode<T> k3 ){
	    k3.left = rotateLeft( k3.left );
	    return rotateRight( k3 );
	}

	//Fixes Right-Left case
	private MyNode<T> doubleRightLeft( MyNode<T> k1 ){
	    k1.right = rotateRight( k1.right );
	    return rotateLeft( k1 );
	}

//////////////////////////////////////////////////////////
	public static void main(String args[]){
		MyAVLTree2<Integer> tree = new MyAVLTree2<>();
		tree.insert(20);
		tree.insert(10);
		tree.insert(7);
		tree.insert(30);
		tree.insert(40);
		tree.insert(15);
		tree.insert(11);
		tree.insert(11);
		tree.insert(11);
		tree.inorder();
		tree.levelTraverse();
		tree.checkBalance();

		//tree.remove(7); //node with no children
		//tree.remove(15); //node with one child
		//tree.remove(10); //interior node with two children, and a node with no children
		//tree.remove(20); //root removal and node with 2 children, node with children
		tree.remove(11);
		tree.remove(11);
		//tree.remove(11);
		tree.inorder();
		tree.levelTraverse();
		tree.checkBalance();
	}


///////////////////////////////////////////////////////////
    public void checkBalance( )
    {
        checkBalance(root);
    }

    private int checkBalance( MyNode<T> temp )
    {
        if( temp == null )
            return -1;

        if( temp != null )
        {
            int hl = checkBalance(temp.left);
            int hr = checkBalance(temp.right);
            if(Math.abs(height(temp.left) - height(temp.right)) > ALLOWED_IMBALANCE ||
                    height(temp.left) != hl || height(temp.right) != hr)
                System.out.println( "OOPS!!" );
        }

        return height(temp);
    }

}
