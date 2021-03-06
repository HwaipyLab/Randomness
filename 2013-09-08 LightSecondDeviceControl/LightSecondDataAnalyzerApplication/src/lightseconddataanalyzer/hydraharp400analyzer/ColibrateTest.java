package lightseconddataanalyzer.hydraharp400analyzer;

import com.hwaipy.unifieddeviceinterface.DeviceException;
import com.hwaipy.unifieddeviceinterface.timeeventdevice.data.TimeEventDataManager;
import com.hwaipy.unifieddeviceinterface.timeeventdevice.data.TimeEventLoader;
import com.hwaipy.unifieddeviceinterface.timeeventdevice.data.process.RecursionCoincidenceMatcher;
import com.hwaipy.unifieddeviceinterface.timeeventdevice.data.process.StandardPeriodTimeEventList;
import com.hwaipy.unifieddeviceinterface.timeeventdevice.data.process.TimeCalibrator;
import com.hwaipy.unifieddeviceinterface.timeeventdevice.hydraharp400data.HydraHarp400T2Loader;
import com.hwaipy.unifieddeviceinterface.timeeventdevice.timeeventcontainer.TimeEventList;
import com.hwaipy.unifieddeviceinterface.timeeventdevice.timeeventcontainer.TimeEventSegment;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Hwaipy
 */
public class ColibrateTest {

    public static void main(String[] args) throws IOException, DeviceException {
        File file = new File("/Users/Hwaipy/Documents/Dropbox/LabWork/实验/2013-12-03 光秒恢复状态采数/2013-12-04 PXI_TDC与HydraHarp400同步测试/20131204211729.ht2");

        TimeEventLoader loader = new HydraHarp400T2Loader(file);
        TimeEventSegment segment = TimeEventDataManager.loadTimeEventSegment(loader);
        TimeEventList syncList = segment.getEventList(8);
        TimeEventList signalList = segment.getEventList(0);
        System.out.println(syncList.size());
        System.out.println(signalList.size());

        StandardPeriodTimeEventList spList = new StandardPeriodTimeEventList(0, 100000000, 600000, 10);
        RecursionCoincidenceMatcher rcm = new RecursionCoincidenceMatcher(spList, syncList, 1000000, syncList.get(0).getTime());
        rcm.find();
        TimeCalibrator timeCalibrator = new TimeCalibrator(rcm, signalList);
        timeCalibrator.calibrate();
//        timeCalibrator.calibrateOverall();
        int index = 0;
        for (int i = 0; i < 1000000; i++) {
            long t = signalList.get(i).getTime();
            if (t > 0) {
                index = i;
                break;
            }
        }
        System.out.println("index is " + index);
        int count = 0;
        long sum = 0;
        for (int i = 1; i < 10000000; i++) {
            long t = signalList.get(index + i).getTime() - signalList.get(index + i - 1).getTime();
            if (t < 0) {
                break;
            }
            System.out.println(t);
//            sum += t;
            sum += (t - 50000000) * (t - 50000000);
            count++;
        }
        System.out.println("Count is " + count);
//        System.out.println(sum / (double) count);
        System.out.println(Math.sqrt(sum / (double) count));
    }
}
