package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation;

import com.hwaipy.measure.unit.Units;
import com.hwaipy.physics.crystaloptics.Axis;
import com.hwaipy.physics.crystaloptics.Medium;
import com.hwaipy.physics.crystaloptics.MonochromaticWave;
import javax.measure.unit.SI;
import org.jscience.physics.amount.Amount;
import org.jscience.physics.amount.Constants;

/**
 *
 * @author Hwaipy
 */
public class QuasiPhaseMatchFunction extends CorrelationFunction {

    private final Medium medium;
    private final double lengthOfCrystal;
    private final double period;

    public QuasiPhaseMatchFunction(Medium medium, double lengthOfCrystalInMM, double periodInUM) {
        this.medium = medium;
        this.lengthOfCrystal = lengthOfCrystalInMM / 1000;
        this.period = periodInUM / 1e6;
    }

    @Override
    public double correlationValue(double lamdaSignal, double lamdaIdler) {
        return correlationValueDirect(lamdaSignal, lamdaIdler);
//        return correlationValueApproximate(lamdaSignal, lamdaIdler);
    }

    public double correlationValueDirect(double lamdaSignal, double lamdaIdler) {
        double lamdaPump = 1 / (1 / lamdaSignal + 1 / lamdaIdler);
        MonochromaticWave pump = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaPump, Units.NANOMETRE));
        MonochromaticWave signal = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaSignal, Units.NANOMETRE));
        MonochromaticWave idle = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaIdler, Units.NANOMETRE));
        double kPump = pump.getWaveNumber(medium, Axis.Y).doubleValue(Units.RECIPROCALMETRE);
        double kSignal = signal.getWaveNumber(medium, Axis.Y).doubleValue(Units.RECIPROCALMETRE);
        double kIdler = idle.getWaveNumber(medium, Axis.Z).doubleValue(Units.RECIPROCALMETRE);

        double arg = lengthOfCrystal / 2 * (kPump - kSignal - kIdler - 2 * Math.PI / period);
        double result = Math.sin(arg) / arg;
        return result;
    }

    public double correlationValueApproximate(double lamdaSignal, double lamdaIdler) {
        double lamdaPump = 1 / (1 / lamdaSignal + 1 / lamdaIdler);
        MonochromaticWave pump = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaPump, Units.NANOMETRE));
        MonochromaticWave signal = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaSignal, Units.NANOMETRE));
        MonochromaticWave idle = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaIdler, Units.NANOMETRE));
        double ngPump = medium.getGroupIndex(pump, Axis.Y);
        double ngSignal = medium.getGroupIndex(signal, Axis.Y);
        double ngIdle = medium.getGroupIndex(idle, Axis.Z);
        MonochromaticWave pdcCenter = MonochromaticWave.byWaveLength(Amount.valueOf(780, Units.NANOMETRE));
        double omegaPDCCenter = pdcCenter.getAngularFrequency().doubleValue(SI.HERTZ);
        double omegaPDCSignal = signal.getAngularFrequency().doubleValue(SI.HERTZ);
        double omegaPDCIdle = idle.getAngularFrequency().doubleValue(SI.HERTZ);
        double vo = omegaPDCSignal - omegaPDCCenter;
        double ve = omegaPDCIdle - omegaPDCCenter;
        double c = Constants.c.doubleValue(SI.METERS_PER_SECOND);
        double kpp = ngPump / c;
        double kop = ngSignal / c;
        double kep = ngIdle / c;
        double r = (vo * (kop - kpp) + ve * (kep - kpp)) * lengthOfCrystal / 2;
        return Math.sin(r) / r;
    }
}