package socket;

import java.util.ArrayList;

import model.Entity;

public abstract class Client {
	public abstract void case1(ArrayList<String> transactions) throws Exception;
	public abstract ArrayList<Entity> getById(String id);
}
