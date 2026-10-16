
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Main {

    public static double randExponential(double rate) {
        double u = Math.random();
        return -Math.log(1-u) / rate;
    }

    public static Stats fcfs(Queue<Event> eq){

        Stats out = new Stats();

        Queue<Process> rq = new LinkedList<>();

        final int processCount = eq.size();

        double timePtr = 0.0;
        double idle_time = 0.0;
        Event next;
        Process pRunning = null;
        while(!(eq.isEmpty() && rq.isEmpty() && pRunning == null)){
            next = eq.remove();
            //System.out.println(next.toString());
            double dt = next.time - timePtr;
            if(rq.isEmpty() && pRunning == null) idle_time += dt;
            out.avg_ready_queue_length += rq.size() * dt;
            timePtr = next.time;
            if(next.type == EventType.ARR){
                if(pRunning == null){
                    pRunning = next.process;
                    pRunning.start_time = timePtr;
                    Event dep = new Event(EventType.DEP, timePtr + next.process.remaining_service_time, next.process);
                    eq.add(dep);
                }
                else{
                    rq.add(next.process);
                }
            }
            else{
                pRunning.completion_time = timePtr;
                out.avg_turnaround_time += pRunning.completion_time - pRunning.arrival_time;
                out.avg_waiting_time += pRunning.completion_time - pRunning.arrival_time - pRunning.service_time;
                out.avg_response_time += pRunning.start_time - pRunning.arrival_time;

                if(rq.isEmpty()) pRunning = null;
                else{
                    pRunning = rq.remove();
                    pRunning.start_time = timePtr;
                    Event dep = new Event(EventType.DEP, timePtr + pRunning.remaining_service_time, pRunning);
                    eq.add(dep);
                }
            }
        }

        out.completion_time = timePtr;
        out.utilization = (timePtr - idle_time) * 100.0 / timePtr;
        out.throughput = processCount / timePtr;
        out.avg_ready_queue_length /= out.completion_time;
        out.avg_response_time /= (double)processCount;
        out.avg_turnaround_time /= (double)processCount;
        out.avg_waiting_time /= (double)processCount;

        return out;
    }



    public static Stats sjf(Queue<Event> eq){

        Stats out = new Stats();

        Comparator<Process> pc = (p1, p2) -> {
            if(p1.service_time == p2.service_time) return p1.id - p2.id;
            return (int)Math.signum(p1.service_time - p2.service_time);
        };

        Queue<Process> rq = new PriorityQueue<>(pc);

        final int processCount = eq.size();

        double timePtr = 0.0;
        double idle_time = 0.0;
        Event next;
        Process pRunning = null;
        while(!(eq.isEmpty() && rq.isEmpty() && pRunning == null)){
            next = eq.remove();
            //System.out.println(next.toString());
            double dt = next.time - timePtr;
            if(rq.isEmpty() && pRunning == null) idle_time += dt;
            out.avg_ready_queue_length += rq.size() * dt;
            timePtr = next.time;
            if(next.type == EventType.ARR){
                if(pRunning == null){
                    pRunning = next.process;
                    pRunning.start_time = timePtr;
                    Event dep = new Event(EventType.DEP, timePtr + next.process.remaining_service_time, next.process);
                    eq.add(dep);
                }
                else{
                    rq.add(next.process);
                }
            }
            else{
                pRunning.completion_time = timePtr;
                out.avg_turnaround_time += pRunning.completion_time - pRunning.arrival_time;
                out.avg_waiting_time += pRunning.completion_time - pRunning.arrival_time - pRunning.service_time;
                out.avg_response_time += pRunning.start_time - pRunning.arrival_time;

                if(rq.isEmpty()) pRunning = null;
                else{
                    pRunning = rq.remove();
                    pRunning.start_time = timePtr;
                    Event dep = new Event(EventType.DEP, timePtr + pRunning.remaining_service_time, pRunning);
                    eq.add(dep);
                }
            }
        }

        out.completion_time = timePtr;
        out.utilization = (timePtr - idle_time) * 100.0 / timePtr;
        out.throughput = processCount / timePtr;
        out.avg_ready_queue_length /= out.completion_time;
        out.avg_response_time /= (double)processCount;
        out.avg_turnaround_time /= (double)processCount;
        out.avg_waiting_time /= (double)processCount;

        return out;
    }



    public static Stats srtf(Queue<Event> eq){

        Stats out = new Stats();

        Comparator<Process> pc = (p1, p2) -> {
            if(p1.remaining_service_time == p2.remaining_service_time) return p1.id - p2.id;
            return (int)Math.signum(p1.remaining_service_time - p2.remaining_service_time);
        };

        Queue<Process> rq = new PriorityQueue<>(pc);

        final int processCount = eq.size();

        double timePtr = 0.0;
        double idle_time = 0.0;
        Event next;
        Process pRunning = null;
        while(!(eq.isEmpty() && rq.isEmpty() && pRunning == null)){
            next = eq.remove();
            //System.out.println(next.toString());
            double dt = next.time - timePtr;
            if(rq.isEmpty() && pRunning == null) idle_time += dt;
            out.avg_ready_queue_length += rq.size() * dt;
            timePtr = next.time;
            if(next.type == EventType.ARR){
                if(pRunning == null){
                    // No processes are in the queue and no process is running
                    pRunning = next.process;
                    pRunning.start_time = timePtr;
                    Event dep = new Event(EventType.DEP, timePtr + next.process.remaining_service_time, next.process);
                    eq.add(dep);
                }
                else{
                    // Process arrives while other process is running
                    // Only condition where preemption can happen
                    Process pNew = next.process;
                    pRunning.remaining_service_time -= dt;
                    if(pNew.remaining_service_time < pRunning.remaining_service_time){
                        for(Event e : eq){
                            // Remove departure event
                            if(e.type == EventType.DEP && e.process.equals(pRunning)){
                                eq.remove(e);
                                break;
                            }
                        }
                        rq.add(pRunning);
                        pRunning = pNew;
                        pRunning.start_time = timePtr;
                        Event dep = new Event(EventType.DEP, timePtr + pRunning.remaining_service_time, pRunning);
                        eq.add(dep);
                    }
                    else rq.add(pNew);
                }
            }
            else{
                pRunning.completion_time = timePtr;
                out.avg_turnaround_time += pRunning.completion_time - pRunning.arrival_time;
                out.avg_waiting_time += pRunning.completion_time - pRunning.arrival_time - pRunning.service_time;
                out.avg_response_time += pRunning.start_time - pRunning.arrival_time;

                if(rq.isEmpty()) pRunning = null;
                else{
                    pRunning = rq.remove();
                    if(pRunning.start_time < 0) pRunning.start_time = timePtr;
                    Event dep = new Event(EventType.DEP, timePtr + pRunning.remaining_service_time, pRunning);
                    eq.add(dep);
                }
            }
        }

        out.completion_time = timePtr;
        out.utilization = (timePtr - idle_time) * 100.0 / timePtr;
        out.throughput = processCount / timePtr;
        out.avg_ready_queue_length /= out.completion_time;
        out.avg_response_time /= (double)processCount;
        out.avg_turnaround_time /= (double)processCount;
        out.avg_waiting_time /= (double)processCount;

        return out;
    }

    public static Stats rr(Queue<Event> eq, final double quantum){

        Stats out = new Stats();

        Queue<Process> rq = new LinkedList<>();

        final int processCount = eq.size();

        double timePtr = 0.0;
        double idle_time = 0.0;
        Event next;
        Process pRunning = null;
        while(!(eq.isEmpty() && rq.isEmpty() && pRunning == null)){
            next = eq.remove();
            //System.out.println(next.toString());
            double dt = next.time - timePtr;
            if(rq.isEmpty() && pRunning == null) idle_time += dt;
            out.avg_ready_queue_length += rq.size() * dt;
            timePtr = next.time;
            if(next.type == EventType.ARR){
                if(pRunning == null){
                    // No processes are running or in the queue
                    pRunning = next.process;
                    pRunning.start_time = timePtr;
                    if(pRunning.remaining_service_time < quantum){
                        eq.add(new Event(EventType.DEP, timePtr + pRunning.remaining_service_time, pRunning));
                    }
                    else eq.add(new Event(EventType.QUANTUM_EXPIRE, timePtr + quantum, pRunning));
                }
                else{
                    rq.add(next.process);
                }
            }
            else if(next.type == EventType.DEP){
                pRunning.completion_time = timePtr;
                out.avg_turnaround_time += pRunning.completion_time - pRunning.arrival_time;
                out.avg_waiting_time += pRunning.completion_time - pRunning.arrival_time - pRunning.service_time;
                out.avg_response_time += pRunning.start_time - pRunning.arrival_time;

                if(rq.isEmpty()) pRunning = null;
                else{
                    pRunning = rq.remove();
                    if(pRunning.start_time < 0) pRunning.start_time = timePtr;
                    if(pRunning.remaining_service_time < quantum){
                        eq.add(new Event(EventType.DEP, timePtr + pRunning.remaining_service_time, pRunning));
                    }
                    else eq.add(new Event(EventType.QUANTUM_EXPIRE, timePtr + quantum, pRunning));
                }
            }
            else{
                if(pRunning == null){
                    // This should never happen, but worth troubleshooting
                    continue;
                }
                pRunning.remaining_service_time -= quantum;
                rq.add(pRunning);
                pRunning = rq.remove();
                if(pRunning.start_time < 0) pRunning.start_time = timePtr;
                if(pRunning.remaining_service_time < quantum){
                    eq.add(new Event(EventType.DEP, timePtr + pRunning.remaining_service_time, pRunning));
                }
                else eq.add(new Event(EventType.QUANTUM_EXPIRE, timePtr + quantum, pRunning));
            }
        }

        out.completion_time = timePtr;
        out.utilization = (timePtr - idle_time) * 100.0 / timePtr;
        out.throughput = processCount / timePtr;
        out.avg_ready_queue_length /= out.completion_time;
        out.avg_response_time /= (double)processCount;
        out.avg_turnaround_time /= (double)processCount;
        out.avg_waiting_time /= (double)processCount;

        return out;
    }

    public static Stats hrrn(Queue<Event> eq){

        Stats out = new Stats();

        Queue<Process> rq = new LinkedList<>();

        final int processCount = eq.size();

        double timePtr = 0.0;
        double idle_time = 0.0;
        Event next;
        Process pRunning = null;
        while(!(eq.isEmpty() && rq.isEmpty() && pRunning == null)){
            next = eq.remove();
            //System.out.println(next.toString());
            double dt = next.time - timePtr;
            if(rq.isEmpty() && pRunning == null) idle_time += dt;
            out.avg_ready_queue_length += rq.size() * dt;
            timePtr = next.time;
            if(next.type == EventType.ARR){
                if(pRunning == null){
                    pRunning = next.process;
                    pRunning.start_time = timePtr;
                    Event dep = new Event(EventType.DEP, timePtr + next.process.remaining_service_time, next.process);
                    eq.add(dep);
                }
                else{
                    rq.add(next.process);
                }
            }
            else{
                pRunning.completion_time = timePtr;
                out.avg_turnaround_time += pRunning.completion_time - pRunning.arrival_time;
                out.avg_waiting_time += pRunning.completion_time - pRunning.arrival_time - pRunning.service_time;
                out.avg_response_time += pRunning.start_time - pRunning.arrival_time;

                if(rq.isEmpty()) pRunning = null;
                else{
                    // There's probably a more elegant/efficient way of doing this.
                    // I can't think of it right now.
                    Process hrr = rq.peek();
                    for(Process p : rq){
                        double ratio1 = ((timePtr - p.arrival_time) + p.service_time) / p.service_time;
                        double ratio2 = ((timePtr - hrr.arrival_time) + hrr.service_time) / hrr.service_time;
                        if(ratio1 > ratio2) hrr = p;
                    }
                    rq.remove(hrr);
                    pRunning = hrr;
                    pRunning.start_time = timePtr;
                    Event dep = new Event(EventType.DEP, timePtr + pRunning.remaining_service_time, pRunning);
                    eq.add(dep);
                }
            }
        }

        out.completion_time = timePtr;
        out.utilization = (timePtr - idle_time) * 100.0 / timePtr;
        out.throughput = processCount / timePtr;
        out.avg_ready_queue_length /= out.completion_time;
        out.avg_response_time /= (double)processCount;
        out.avg_turnaround_time /= (double)processCount;
        out.avg_waiting_time /= (double)processCount;

        return out;
    }

    public static void main(String[] args) {
        final int PROCESS_COUNT = 1000;

        // Create comparator to time-sort event queue
        Comparator<Event> ec = (e1, e2) -> {
            return (int)Math.signum(e1.time - e2.time);
        };

        PriorityQueue<Event> eq = new PriorityQueue<>(ec);
        double aTime = 0.0;

        // Create arrival events to be used in simulations
        for(int i=0;i<PROCESS_COUNT;i++){
            Process p = new Process();
            p.id = i+1;
            double interTime = randExponential(2);
            p.arrival_time = aTime + interTime;
            aTime += interTime;
            p.service_time = randExponential(1);
            p.start_time = -1.0;
            p.remaining_service_time = p.service_time;
            Event e = new Event(EventType.ARR, p.arrival_time, p);
            eq.add(e);
        }

        PriorityQueue<Event> eqCopy;

        Stats[] statsArr = new Stats[5];

        // Event queue must be deep copied each time since it is passed by reference
        eqCopy = new PriorityQueue<>(ec);
        for(Event e : eq) eqCopy.add(e.copy());
        statsArr[0] = fcfs(eqCopy);

        eqCopy = new PriorityQueue<>(ec);
        for(Event e : eq) eqCopy.add(e.copy());
        statsArr[1] = sjf(eqCopy);

        eqCopy = new PriorityQueue<>(ec);
        for(Event e : eq) eqCopy.add(e.copy());
        statsArr[2] = srtf(eqCopy);

        eqCopy = new PriorityQueue<>(ec);
        for(Event e : eq) eqCopy.add(e.copy());
        statsArr[3] = rr(eqCopy, 2.0);

        eqCopy = new PriorityQueue<>(ec);
        for(Event e : eq) eqCopy.add(e.copy());
        statsArr[4] = hrrn(eqCopy);

        try{
            FileWriter writer = new FileWriter("output.md");
            writer.write("### CS 3360 Assignment 3 Performance Summary (1000 Processes)\n\n\n");
            writer.write("| Metric | FCFS | SJF | SRTF | RR (q=2.0) | HRRN |\n");
            writer.write("| :--- | :---: | :---: | :---: | :---: | :---: |\n");

            writer.write("| Total Completion Time (ms) |");
            for(Stats s : statsArr) writer.write(String.format("%.3f|", s.completion_time));
            writer.write("\n");

            writer.write("| Average Turnaround Time (ms) |");
            for(Stats s : statsArr) writer.write(String.format("%.3f|", s.avg_turnaround_time));
            writer.write("\n");

            writer.write("| Average Waiting Time (ms) |");
            for(Stats s : statsArr) writer.write(String.format("%.3f|", s.avg_waiting_time));
            writer.write("\n");

            writer.write("| Average Response Time (ms) |");
            for(Stats s : statsArr) writer.write(String.format("%.3f|", s.avg_response_time));
            writer.write("\n");

            writer.write("| CPU Utilization (%) |");
            for(Stats s : statsArr) writer.write(String.format("%.2f|", s.utilization));
            writer.write("\n");

            writer.write("| System Throughput (proc/s) |");
            for(Stats s : statsArr) writer.write(String.format("%.2f|", s.throughput));
            writer.write("\n");

            writer.write("| Avg. Ready Queue Length |");
            for(Stats s : statsArr) writer.write(String.format("%.2f|", s.avg_ready_queue_length));
            writer.write("\n");

            writer.flush();
            writer.close();

            System.out.println("Output saved to output.md");
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}