public class Process {
    public int id;
    public double arrival_time;
    public double service_time;
    public double remaining_service_time;
    public double start_time;
    public double completion_time;

    @Override
    public String toString(){
        return "[service=" + service_time + ", arrival=" + arrival_time + "]";
    }

    public boolean equals(Process p){
        return p.id == this.id;
    }

}
