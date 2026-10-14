
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
                if(pRunning == null){
                    // This should never happen, but worth troubleshooting
                    System.out.println("what?");
                    continue;
                }
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
        out.throughput = process_count / timePtr;
        out.avg_ready_queue_length /= out.completion_time;
        out.avg_response_time /= (double)process_count;
        out.avg_turnaround_time /= (double)process_count;
        out.avg_waiting_time /= (double)process_count;

        return out;
    }



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
                if(pRunning == null){
                    // This should never happen, but worth troubleshooting
                    System.out.println("what?");
                    continue;
                }
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
        out.throughput = process_count / timePtr;
        out.avg_ready_queue_length /= out.completion_time;
        out.avg_response_time /= (double)process_count;
        out.avg_turnaround_time /= (double)process_count;
        out.avg_waiting_time /= (double)process_count;

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
            p.start_time = -1.0; // Negative if process hasn't started, used for preemption
            p.remaining_service_time = p.service_time;
            Event e = new Event(EventType.ARR, p.arrival_time, p);
            eq.add(e);
        }

        System.out.println("TOTAL TIME: " + test);

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
                if(pRunning == null){
                    // This should never happen, but worth troubleshooting
                    System.out.println("what?");
                    continue;
                }
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
        out.throughput = process_count / timePtr;
        out.avg_ready_queue_length /= out.completion_time;
        out.avg_response_time /= (double)process_count;
        out.avg_turnaround_time /= (double)process_count;
        out.avg_waiting_time /= (double)process_count;

        return out;
    }

    public static Stats rr(final int process_count, final double quantum){

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
            p.start_time = -1.0;
            p.remaining_service_time = p.service_time;
            Event e = new Event(EventType.ARR, p.arrival_time, p);
            eq.add(e);
        }

        System.out.println("TOTAL TIME: " + test);

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
                    else eq.add(new Event(EventType.QUANTUM_EXPIRE, timePtr + quantum, null));
                }
                else{
                    rq.add(next.process);
                }
            }
            else if(next.type == EventType.DEP){
                if(pRunning == null){
                    // This should never happen, but worth troubleshooting
                    continue;
                }
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
                    else eq.add(new Event(EventType.QUANTUM_EXPIRE, timePtr + quantum, null));
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
                else eq.add(new Event(EventType.QUANTUM_EXPIRE, timePtr + quantum, null));
            }
        }

        out.completion_time = timePtr;
        out.utilization = (timePtr - idle_time) * 100.0 / timePtr;
        out.throughput = process_count / timePtr;
        out.avg_ready_queue_length /= out.completion_time;
        out.avg_response_time /= (double)process_count;
        out.avg_turnaround_time /= (double)process_count;
        out.avg_waiting_time /= (double)process_count;

        return out;
    }

    public static Stats hrrn(final int process_count){

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
                if(pRunning == null){
                    // This should never happen, but worth troubleshooting
                    System.out.println("what?");
                    continue;
                }
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
        out.throughput = process_count / timePtr;
        out.avg_ready_queue_length /= out.completion_time;
        out.avg_response_time /= (double)process_count;
        out.avg_turnaround_time /= (double)process_count;
        out.avg_waiting_time /= (double)process_count;

        return out;
    }

    public static void main(String[] args) {
        final int PROCESS_COUNT = 1000;
        fcfs(PROCESS_COUNT).print();
        sjf(PROCESS_COUNT).print();
        srtf(PROCESS_COUNT).print();
        rr(PROCESS_COUNT, 2.0).print();
    }
}