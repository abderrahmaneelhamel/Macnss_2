package macnss.model;

public class Patient extends User {
    private int matricule;
    private int days;
    private Double salary;
    private int company_id;
    private String status;
    private String BirthDate;

    public Patient(int id, String name, String email, String password, int matricule,String  BirthDate) {
        super(id, name, email, password);
        this.matricule = matricule;
        this.BirthDate = BirthDate;
        this.days = 0;
        this.salary = 0.0;
        this.company_id = 0;
        this.status = null;
    }

    public Patient(int id, String name, String email, String password, int matricule, String BirthDate, int days, Double salary, int company_id, String status) {
        super(id, name, email, password);
        this.matricule = matricule;
        this.BirthDate = BirthDate;
        this.days = days;
        this.salary = salary;
        this.company_id = company_id;
        this.status = status;
    }

    public void setMatricule(int matricule) {
        this.matricule = matricule;
    }

    public int getMatricule() {
        return this.matricule;
    }

    public void setBirthDate(String BirthDate) {
        this.BirthDate = BirthDate;
    }

    public String getBirthDate() {
        return this.BirthDate;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public String getStatus() {
        return status;
    }
    public String getRetirementStatus() {
        if (status.equals("retired")){
            return "retired";
        }
        else{
            return "not yet retired";
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
