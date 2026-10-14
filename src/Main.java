
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Main {

    public static double randExponential(double rate) {
        double u = Math.random();
        return -Math.log(1-u) / rate;
    }

    public static Stats fcfs(final int process_count){

        Stats out = new Stats();

        Comparator<Event> ec = (e1, e2) -> {
            return (int)Math.signum(e1.time - e2.time);
        };

        PriorityQueue<Event> eq = new PriorityQueue<>(ec);
        Queue<Process> rq = new LinkedList<>();
        double aTime = 0.0;
        double test = 0.0;
        for(int i=0;i<process_count;i++){
            Process p = new Process();
            p.id = i+1;
            double interTime = randExponential(2);
            p.arrival_time = aTime + interTime;
            aTime += interTime;
            p.service_time = randExponential(1);
            test += p.service_time;
            p.remaining_service_time = p.service_time;
            Event e = new Event(EventType.ARR, p.arrival_time, p);
            eq.add(e);
        }

        System.out.println("TOTAL TIME: " + test);

        double timePtr = 0.0;
        double idle_time = 0.0;
        Process pRunning = null;
        while(!(eq.isEmpty() && rq.isEmpty() && pRunning == null)){
            if(pRunning == null){
                if(timePtr < eq.peek().time){
                    // CPU is idle
                    idle_time += eq.peek().time - timePtr;
                    timePtr = eq.peek().time;
                }
                else{
                    pRunning = eq.remove().process;
                    pRunning.start_time = timePtr;
                    pRunning.completion_time = timePtr + pRunning.service_time;
                }
            }
            else{
                if(!eq.isEmpty() && eq.peek().time < pRunning.completion_time){
                    timePtr = eq.peek().time;
                    rq.add(eq.remove().process);
                }
                else{
                    timePtr = pRunning.completion_time;
                    if(!rq.isEmpty()){
                        pRunning = rq.remove();
                        pRunning.start_time = timePtr;
                        pRunning.completion_time = timePtr + pRunning.service_time;
                    }
                    else{
                        pRunning = null;
                    }
                }
            }
        }

        out.completion_time = timePtr;
        out.utilization = (timePtr - idle_time) * 100.0 / timePtr;
        out.throughput = process_count / timePtr;
        out.avg_ready_queue_length /= (double)process_count;
        out.avg_response_time /= (double)process_count;
        out.avg_turnaround_time /= (double)process_count;
        out.avg_waiting_time /= (double)process_count;

        return out;
    }


    /**
     * Schedules processes by shortest job first
     * @param process_count Amount of processes to schedule
     * @return Statistics about running time of algorithm
     */
    public static Stats sjf(final int process_count){

        Stats out = new Stats();

        Comparator<Event> ec = (e1, e2) -> {
            return (int)Math.signum(e1.time - e2.time);
        };

        Comparator<Process> pc = (p1, p2) -> {
            return (int)Math.signum(p1.service_time - p2.service_time);
        };

        PriorityQueue<Event> eq = new PriorityQueue<>(ec);
        Queue<Process> rq = new PriorityQueue<>(pc);
        double aTime = 0.0;
        double test = 0.0;
        for(int i=0;i<process_count;i++){
            Process p = new Process();
            p.id = i+1;
            double interTime = randExponential(2);
            p.arrival_time = aTime + interTime;
            aTime += interTime;
            p.service_time = randExponential(1);
            test += p.service_time;
            p.remaining_service_time = p.service_time;
            Event e = new Event(EventType.ARR, p.arrival_time, p);
            eq.add(e);
        }

        System.out.println("TOTAL TIME: " + test);

        double timePtr = 0.0;
        double idle_time = 0.0;
        Process pRunning = null;
        while(!(eq.isEmpty() && rq.isEmpty() && pRunning == null)){
            if(pRunning == null){
                if(timePtr < eq.peek().time){
                    // CPU is idle
                    idle_time += eq.peek().time - timePtr;
                    timePtr = eq.peek().time;
                }
                else{
                    pRunning = eq.remove().process;
                    pRunning.start_time = timePtr;
                    pRunning.completion_time = timePtr + pRunning.service_time;
                }
            }
            else{
                if(!eq.isEmpty() && eq.peek().time < pRunning.completion_time){
                    timePtr = eq.peek().time;
                    rq.add(eq.remove().process);
                }
                else{
                    timePtr = pRunning.completion_time;
                    if(!rq.isEmpty()){
                        pRunning = rq.remove();
                        pRunning.start_time = timePtr;
                        pRunning.completion_time = timePtr + pRunning.service_time;
                    }
                    else{
                        pRunning = null;
                    }
                }
            }
        }

        out.completion_time = timePtr;
        out.utilization = (timePtr - idle_time) * 100.0 / timePtr;

        return out;
    }



    public static Stats srtf(final int process_count){

        Stats out = new Stats();

        Comparator<Event> ec = (e1, e2) -> {
            return (int)Math.signum(e1.time - e2.time);
        };

        Comparator<Process> pc = (p1, p2) -> {
            return (int)Math.signum(p1.remaining_service_time - p2.remaining_service_time);
        };

        PriorityQueue<Event> eq = new PriorityQueue<>(ec);
        Queue<Process> rq = new PriorityQueue<>(pc);
        double aTime = 0.0;
        double test = 0.0;
        for(int i=0;i<process_count;i++){
            Process p = new Process();
            p.id = i+1;
            double interTime = randExponential(2);
            p.arrival_time = aTime + interTime;
            aTime += interTime;
            p.service_time = randExponential(1);
            test += p.service_time;
            p.remaining_service_time = p.service_time;
            Event e = new Event(EventType.ARR, p.arrival_time, p);
            eq.add(e);
        }

        System.out.println("TOTAL TIME: " + test);

        double timePtr = 0.0;
        double idle_time = 0.0;
        Process pRunning = null;
        while(!(eq.isEmpty() && rq.isEmpty() && pRunning == null)){
            if(pRunning == null){
                if(timePtr < eq.peek().time){
                    // CPU is idle
                    idle_time += eq.peek().time - timePtr;
                    timePtr = eq.peek().time;
                }
                else{
                    pRunning = eq.remove().process;
                    pRunning.start_time = timePtr;
                    pRunning.completion_time = timePtr + pRunning.service_time;
                }
            }
            else{
                if(!eq.isEmpty() && eq.peek().time < pRunning.completion_time){
                    timePtr = eq.peek().time;
                    rq.add(eq.remove().process);
                }
                else{
                    timePtr = pRunning.completion_time;
                    if(!rq.isEmpty()){
                        pRunning = rq.remove();
                        pRunning.start_time = timePtr;
                        pRunning.completion_time = timePtr + pRunning.service_time;
                    }
                    else{
                        pRunning = null;
                    }
                }
            }
        }

        out.completion_time = timePtr;
        out.utilization = (timePtr - idle_time) * 100.0 / timePtr;

        return out;
    }

    public static void main(String[] args) {
        final int PROCESS_COUNT = 1000;
        Stats s = fcfs(PROCESS_COUNT);
        System.out.println(s.completion_time);
        System.out.println(s.utilization);
        s = sjf(PROCESS_COUNT);
        System.out.println(s.completion_time);
        System.out.println(s.utilization);
    }
}