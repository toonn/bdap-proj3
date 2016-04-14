/**
 * SimilarPair contains the ids of two objects and their similarity.
 * 
 * @author Toon Van Craenendonck
 *
 */
public class SimilarPair implements Comparable<SimilarPair>{
	int id1;
	int id2;
	double sim;
	
	/**
	 * Construct a SimilarPair object
	 * @param id1 id of object 1
	 * @param id2 id of object 2
	 * @param sim their similarity
	 */
	public SimilarPair(int id1, int id2, double sim){
		this.id1 = id1;
		this.id2 = id2;
		this.sim = sim;
	}

	/**
	 * Comparing a SimilarPair object to another SimilarPair object.
	 */
	@Override
	public int compareTo(SimilarPair c) {
		if (sim < c.getSimilarity()){
			return -1; 
		}else if (sim == c.getSimilarity()){
			return 0;
		}else{
			return 1;
		}
	}
	
	/**
	 * Returns the id of object 1.
	 */
	public int getId1() {
		return id1;
	}

	/**
	 * Returns the id of object 2.
	 */
	public int getId2() {
		return id2;
	}

	/**
	 * Returns the similarity between the objects.
	 */
	public double getSimilarity(){
		return sim;
	}

	@Override
	public boolean equals(Object obj) {
          if (!(obj instanceof SimilarPair)) {
            return false;
          } else {
            SimilarPair o = (SimilarPair) obj;
            return id1 == o.getId1() && id2 == o.getId2() && sim == o.getSimilarity();
          }
	}
	
        /**
         * Based on a stackoverflow post, any proper hashfunction of the fields
         * in equals should do.
         */
        @Override
        public int hashCode() {
          int result = 42;
          result = 37 * result + id1;
          result = 37 * result + id2;
          long l = Double.doubleToLongBits(sim);
          result = 37 * result + (int)(l ^ (l >>> 32));;
          return result;
        }

}
