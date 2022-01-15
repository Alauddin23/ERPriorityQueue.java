import java.util.ArrayList;
import java.util.HashMap;

public class ERPriorityQueue{

    public ArrayList<Patient>  patients;
    public HashMap<String,Integer>  nameToIndex;

    public ERPriorityQueue(){

        //  use a dummy node so that indexing starts at 1, not 0

        patients = new ArrayList<Patient>();
        patients.add( new Patient("dummy", 0.0) );

        nameToIndex  = new HashMap<String,Integer>();
    }

    private int parent(int i){
        return i/2;
    }

    private int leftChild(int i){
        return 2*i;
    }

    private int rightChild(int i){
        return 2*i+1;
    }

    public void swap(int firstIndexToBeSwapped, int secondIndexToBeSwapped){

        String nameOfFirstPatient = this.patients.get(firstIndexToBeSwapped).getName();
        String nameOfSecondPatient = this.patients.get(secondIndexToBeSwapped).getName();

        ERPriorityQueue.Patient patientAtFirstIndex = this.patients.get(firstIndexToBeSwapped);
        ERPriorityQueue.Patient patientAtSecondIndex = this.patients.get(secondIndexToBeSwapped);

       this.patients.set(secondIndexToBeSwapped,patientAtFirstIndex);
       this.patients.set(firstIndexToBeSwapped,patientAtSecondIndex);

        this.nameToIndex.put(nameOfFirstPatient, secondIndexToBeSwapped);
        this.nameToIndex.put(nameOfSecondPatient, firstIndexToBeSwapped);
    }

    public void upHeap(int i){

        int index = i;

        while((index > 1) && (this.patients.get(index).getPriority() < this.patients.get(parent(index)).getPriority())){

            swap(index, parent(index));

            index = parent(index);
        }

    }

    public void downHeap(int i) {

        int index = i;

        int sizeOfPatientList = this.patients.size();

        while (leftChild(index) <= sizeOfPatientList - 1) {

            int childIndex = leftChild(index);

            if (childIndex < sizeOfPatientList - 1) {

                if (this.patients.get(rightChild(index)).getPriority() < this.patients.get(leftChild(index)).getPriority()) {
                    childIndex = rightChild(index);
                } else if (this.patients.get(rightChild(index)).getPriority() == this.patients.get(leftChild(index)).getPriority()) {
                    childIndex = leftChild(index);
                }
            }

            if (this.patients.get(childIndex).getPriority() < this.patients.get(index).getPriority()) {
                swap(index, childIndex);
                index = childIndex;
            } else return;
        }

    }


    public boolean contains(String name){

        if( this.nameToIndex.get(name) != null){
            return true;
        }
        return false;
    }

    public double getPriority(String name){

        if(this.contains(name)){
            int patientIndex = this.nameToIndex.get(name);
            return(this.patients.get(patientIndex).getPriority());
        }
        return -1;
    }

    public double getMinPriority(){

        if(this.patients.size() > 1){
            return(this.patients.get(1).getPriority());
        }
        return -1;
    }

    public String removeMin(){

        if(patients.size() > 1){

            String nameOfPatientWithMinPriority = this.patients.get(1).getName();
            swap(1,(patients.size() - 1));
            patients.remove(patients.size() - 1);
            nameToIndex.remove(nameOfPatientWithMinPriority);
            downHeap(1);
            return nameOfPatientWithMinPriority;
        }
        return null;
    }

    public String peekMin(){
        if(this.patients.size() > 1){
            return(this.patients.get(1).getName());
        }
        return null;
    }

    public boolean  add(String name, double priority){

        if(!(this.contains(name))){

            Patient newPatient = new Patient(name, priority);
            int indexOfNewPatient = this.patients.size();
            this.patients.add(newPatient);
            this.nameToIndex.put(name,indexOfNewPatient);
            this.upHeap(indexOfNewPatient);
            return true;
        }
        return false;
    }

    public boolean  add(String name){

        if (!(this.contains(name))){

            Patient newPatient = new Patient(name, Double.POSITIVE_INFINITY);
            int indexOfNewPatient = this.patients.size();
            this.patients.add(newPatient);
            this.nameToIndex.put(name,indexOfNewPatient);
            return true;
        }

        return false;
    }

    public boolean remove(String name){

        if(this.contains(name)){
            int indexOfPatientToBeRemoved = this.nameToIndex.get(name);
            this.swap(indexOfPatientToBeRemoved, this.patients.size() - 1);
            this.patients.remove(this.patients.size() - 1);
            this.nameToIndex.remove(name);
            this.downHeap(indexOfPatientToBeRemoved);
            return true;
        }
        return false;
    }



    public boolean changePriority(String name, double priority){
        if(this.contains(name)){
            int indexOfPatient = this.nameToIndex.get(name);
            if(this.patients.get(indexOfPatient).getPriority() == priority){
                return true;
            }
            this.patients.get(indexOfPatient).setPriority(priority);
            this.upHeap(indexOfPatient);
            this.downHeap(indexOfPatient);
            return true;
        }
        return false;
    }

    public ArrayList<Patient> removeUrgentPatients(double threshold){

        ArrayList<ERPriorityQueue.Patient> urgentPatientList = new ArrayList<>();

        if(this.patients.size() > 1){

            for(int i = 1; i < this.patients.size(); i++){
                if(this.patients.get(i).getPriority() <= threshold){

                    urgentPatientList.add(this.patients.get(i));
                    this.remove(this.patients.get(i).getName());
                    i--;
                }
            }
        }

        return urgentPatientList;
    }

    public ArrayList<Patient> removeNonUrgentPatients(double threshold){

        ArrayList<ERPriorityQueue.Patient> nonurgentPatientList = new ArrayList<>();

        if(this.patients.size() > 1){

            for(int i = 1; i < this.patients.size(); i++){
                if(this.patients.get(i).getPriority() >= threshold){

                    nonurgentPatientList.add(this.patients.get(i));
                    this.remove(this.patients.get(i).getName());
                    i--;
                }
            }
        }

        return nonurgentPatientList;
    }



    static class Patient{
        private String name;
        private double priority;

        Patient(String name,  double priority){
            this.name = name;
            this.priority = priority;
        }

        Patient(Patient otherPatient){
            this.name = otherPatient.name;
            this.priority = otherPatient.priority;
        }

        double getPriority() {
            return this.priority;
        }

        void setPriority(double priority) {
            this.priority = priority;
        }

        String getName() {
            return this.name;
        }

        @Override
        public String toString(){
            return this.name + " - " + this.priority;
        }

        public boolean equals(Object obj){
            if (!(obj instanceof  ERPriorityQueue.Patient)) return false;
            Patient otherPatient = (Patient) obj;
            return this.name.equals(otherPatient.name) && this.priority == otherPatient.priority;
        }

    }

    public static void main(String[] args){
        ArrayList<ERPriorityQueue.Patient> testPatients = new ArrayList<>();
        HashMap<String,Integer> testNameToIndex = new HashMap<String,Integer>();

        ERPriorityQueue testPriorityQueue = new ERPriorityQueue();

        testPatients.add(new ERPriorityQueue.Patient("dummy", 0.0) );
        testPatients.add(new ERPriorityQueue.Patient("Hannah",5));
        testPatients.add(new ERPriorityQueue.Patient("Ebony",10));
        testPatients.add(new ERPriorityQueue.Patient("Ahmad",15));
        testPatients.add(new ERPriorityQueue.Patient("Zil",30));
        testPatients.add(new ERPriorityQueue.Patient("Ricardo",60));
        testPatients.add(new ERPriorityQueue.Patient("Yinou",50));
        testPatients.add(new ERPriorityQueue.Patient("Gilbert",100));
        testNameToIndex.put(testPatients.get(1).getName(),1);
        testNameToIndex.put(testPatients.get(2).getName(),2);
        testNameToIndex.put(testPatients.get(3).getName(),3);
        testNameToIndex.put(testPatients.get(4).getName(),4);
        testNameToIndex.put(testPatients.get(5).getName(),5);
        testNameToIndex.put(testPatients.get(6).getName(),6);
        testNameToIndex.put(testPatients.get(7).getName(),7);

        testPriorityQueue.patients = new ArrayList<>();
        // Deep copying the patients list so that we can freely mutate our test Priority queue
        for (ERPriorityQueue.Patient patient : testPatients){
            testPriorityQueue.patients.add(new ERPriorityQueue.Patient(patient));
        }
        testPriorityQueue.nameToIndex = new HashMap<>(testNameToIndex);

        ArrayList<Patient> list1 = new ArrayList<>();
        list1 = testPriorityQueue.removeNonUrgentPatients(15);
        System.out.println(testPriorityQueue.patients.get(0));
        System.out.println(testPriorityQueue.patients.get(1));
        System.out.println(testPriorityQueue.nameToIndex.get("Ahmad"));

    }
    }

