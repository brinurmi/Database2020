package Database_TermProject;

import java.util.ArrayList;

//------------------------------------------------------------------------------------------------------//
// Notes:                                                                                               //
//      - Attributes: Unique identifier, name, species, birth date, adoption price and list of          //
//              personality traits                                                                      //
//      - The traits will be parsed and stored as a String array                                        //
//      - Variable "animalID" is auto-assigned when preparedStatement is executed                       //
//          See: AnimalDAO.insert()                                                                     //
//      - Once adopted, an animal is removed from the databases (including its reviews and traits)      //
//------------------------------------------------------------------------------------------------------//

public class Animal {
	protected int animalID;
	protected String name;
	protected String species;
	protected String birthDate;
	protected int adoptionPrice;
	protected String ownersUsername;
	protected ArrayList<String> animalsTraits;

	public Animal() {
	}
	
	public Animal(int animalID) {
		this.animalID = animalID;
	}
	
	public Animal(int animalID, String name, String species, String birthDate, int adoptionPrice, String ownerUsername) {
	    this(name, species, birthDate, adoptionPrice, ownerUsername);
		this.animalID = animalID;
		animalsTraits = null;
	}

    //Animal(animalID, name, species, birthDate, adoptionPrice, ownerUsername, traits);
	public Animal(String name, String species, String birthDate, int adoptionPrice, String ownerUsername) {
        this.name = name;
		this.species = species;
		this.birthDate = birthDate;
		this.adoptionPrice = adoptionPrice;
		this.ownersUsername = ownerUsername;
		animalsTraits = null;
	}

	// CHECK: Can traits stay in one long string? And only parse for searching by trait

	// Used when traits need to be loaded (See: AnimalDAO.listAllAnimals())
	public Animal(int animalID, String name, String species, String birthDate,
                  int adoptionPrice, String ownerUsername, ArrayList<String> traits){
        this.animalID = animalID;
	    this.name = name;
		this.species = species;
		this.birthDate = birthDate;
		this.adoptionPrice = adoptionPrice;
		this.ownersUsername = ownerUsername;
		this.animalsTraits = traits;

		System.out.println("CONSTRUCTOR 3");
	}

	public void setID(int animalID) {
	    this.animalID = animalID;
    }
	public void setName(String name) { this.name = name; }
	public void setSpecies(String species) { this.species = species; }
	public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
	public void setAdoptionPrice(int adoptionPrice) { this.adoptionPrice = adoptionPrice; }
    public void setOwnerUsername(String ownerUsername) { this.ownersUsername = ownerUsername; }

    public int getID() { return animalID; }
	public String getName() { return name; }
	public String getSpecies() { return species; }
	public String getBirthDate() { return birthDate; }
    public int getAdoptionPrice() { return adoptionPrice; }
    public String getOwnerUsername() { return ownersUsername; }

}