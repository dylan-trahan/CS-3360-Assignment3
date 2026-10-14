public class Event {
    public EventType type;
    public double time;
    public Process process;

    public Event(EventType type, double time, Process process){
        this.type = type;
        this.time = time;
        this.process = process;
    }

    @Override
    public String toString(){
        return "[type=" + type + ", time=" + time + ", process=" + process.toString() + "]"; 
    }
}
