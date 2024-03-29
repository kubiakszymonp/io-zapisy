package Entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import Repositories.CompetencyRepository;
import Repositories.RepositoryFactorySingleton;

public class PersonalData extends AbstractEntity {

	private CompetencyRepository competencyRepo;
	public String name;
	public String surname;
	public String PESEL;
	public String address;
	public String phoneNumber;
	public Account account;
	public int yearOfStudy;
	public String educationSubject;
	public List<Competency> competencies;
	public PersonalData(int id, String name, String surname, String pESEL, String address, String phoneNumber,
			Account account,int yearOfStudy,String educationSubject, List<Competency> competencies ) {
		super(id);
		this.name = name;
		this.surname = surname;
		PESEL = pESEL;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.yearOfStudy = yearOfStudy;
		this.educationSubject = educationSubject;
		this.account = account;
		this.competencies = new LinkedList<Competency>();
		this.competencyRepo = RepositoryFactorySingleton.getInstance().getRepository(CompetencyRepository.class);
	}
	
	
	public List<Competency> getCompetencies() {
		return this.competencies;
	}
	
	public PersonalData addCompetency(String name){
		
		final String fName = name;
		final int fId = this.id;
		var a =this.competencyRepo.getAll();
		if(this.id == 0 || 
				this.competencyRepo.getAll().stream()
				.anyMatch(comp -> comp.teacherId == fId && comp.name.equals(fName))) {
			return null;
		}
		
		Competency c = this.competencyRepo.create(new Competency(0, this.id, name));
		this.competencies.add(c);
		
		return this;
	}
	public PersonalData removeCompetency(Competency c){
		
		String fName = c.name;
		final int fId = this.id;
		// if this competency exist
		Optional<Competency> oc = this.competencies.stream().filter(comp -> comp.teacherId == fId || comp.name == fName).findFirst();
		if(oc.isPresent()) {
			Competency com =  oc.get();
			this.competencyRepo.delete(com);
			this.competencies.remove(com);
			return this;
		}
		return null;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
