import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Objects;

public class Location 
{
	private int x;
	private int y;
	private int z;

	public Location(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;

		if(x+y+z!=0)
		{
			throw new InvalidParameterException("x+y+z must equal zero");
		}
	}

	public Location(int x, int y)
	{
		this(x,y,-(x+y));
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public Location getAdjacent(Orientation md)
	{
		int newX = x + md.getDx();
		int newY = y + md.getDy();
		int newZ = z + md.getDz();

		return new Location(newX, newY, newZ);
	}


	public ArrayList<Location> getSurroundingLocations()
	{
		ArrayList<Location> locs=new ArrayList<Location>();
		Orientation[] mds = {Orientation.getSouthEast(), Orientation.getEast(), 
				Orientation.getSouthWest(), Orientation.getNorthWest(), 
				Orientation.getWest(), Orientation.getNorthEast()};

		Location loc=this;

		for(int i=0; i<mds.length; i++)
		{
			locs.add(loc);
			loc = loc.getAdjacent(mds[i]);
		}

		return locs;
	}

	public boolean equals(Object o)
	{
		if(!(o instanceof Location))
		{
			return false;
		}

		Location target = (Location)o;
		return x == target.x && y == target.y && z == target.z;
	}

	public int hashCode(){
		return Objects.hash(x, y);
	}

	public String toString()
	{
		return "("+ x +", " + y + ", " + z + ")";
	}
}