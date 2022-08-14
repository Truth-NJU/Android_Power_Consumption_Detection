package com.example.androidpowercomsumption.utils.diff;

import com.example.androidpowercomsumption.utils.ProcState;

import java.util.ArrayList;
import java.util.List;

public class ThreadConsumptionDiff {

    /**
     * 计算时间段内线程的功耗
     *
     * @param preProcState
     * @param curProcState
     * @return
     */
    public List<ThreadDiff> calculateDiff(List<ProcState> preProcState, List<ProcState> curProcState) {
        List<ThreadDiff> threadDiffList = new ArrayList<>();
        for (ProcState procState1 : preProcState) {
            for (ProcState procState2 : curProcState) {
                if (procState1.getId() == procState2.getId()  && procState1.getComm().equals(procState2.getComm())){
                    ThreadDiff threadDiff = new ThreadDiff();
                    threadDiff.comm = procState1.getComm();
                    threadDiff.state = procState2.getStat();
                    threadDiff.jiffiesDiff = procState2.getJiffies() - procState1.getJiffies();
                    threadDiff.tid = procState2.getId();
                    threadDiffList.add(threadDiff);
                }
            }
        }
        return threadDiffList;

    }

    public static class ThreadDiff {

        public long jiffiesDiff;

        public String comm;

        public String state;

        public int tid;

        @Override
        public String toString() {
            return "ThreadDiff{" +
                    "jiffiesDiff=" + jiffiesDiff +
                    ", comm='" + comm + '\'' +
                    ", state='" + state + '\'' +
                    ", tid=" + tid +
                    '}';
        }
    }
}
