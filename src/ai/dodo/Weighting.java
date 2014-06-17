package ai.dodo;
import kb.*;
import kb.unit.Unit; 
import java.util.ArrayList;

class Weighting {

	public static float c_normal = 1.0f;
	public static float c_main = 1.5f; 
	public static float c_shared = 0.5f; 
	public static float c_enemy = 0.25f; 

	public static float[] getCenterWeights(ArrayList<AdjSupplyCenter> centers, Power power) {
		float[] result = new float[centers.size()];

		for (int i = 0; i < centers.size(); ++i) {
			int sharedUnits = getSharedUnits(centers.get(i), centers);
			int support = centers.get(i).adjUnits.size();
			int negSupport = centers.get(i).supportNeeded; 
			int negShared = getSharedEnemyUnits(centers.get(i), centers);

			if (support == 0) {
				result[i] = 0; 
			} else {
				result[i] = c_normal - c_shared * ((float)sharedUnits / support);  

				if (negSupport != 0) {
					result[i] -= c_enemy * ((float)negSupport / support) * (1.0f - (float) negShared / negSupport);
				}
			}
		}

		return result; 
	}

	private static int getSharedUnits(AdjSupplyCenter c_own, ArrayList<AdjSupplyCenter> centers) {
		int num = 0;

		for (int i = 0; i < c_own.adjUnits.size(); ++i) {
			Unit unit = c_own.adjUnits.get(i); 

			for (AdjSupplyCenter c_adj : centers) {
				if (c_own == c_adj) continue;

				if (c_adj.adjUnits.contains(unit)) {
					num++;
					break;
				}
			}
		}

		return num; 
	}

	private static int getSharedEnemyUnits(AdjSupplyCenter c_own, ArrayList<AdjSupplyCenter> centers) {
		if (c_own.mainEnemy == -1) return 0; 

		int num = 0;
		ArrayList<Unit> units = c_own.adjEnemyUnits.get(c_own.mainEnemy); 

		for (int i = 0; i < units.size(); ++i) {
			Unit unit = units.get(i); 

			for (AdjSupplyCenter c_adj : centers) {
				if (c_own == c_adj) continue;

				ArrayList<Unit> otherUnits = c_adj.adjEnemyUnits.get(c_own.mainEnemy); 
				if (otherUnits.contains(unit)) {
					num++;
					break;
				}
			}
		}

		return num; 
	}

}