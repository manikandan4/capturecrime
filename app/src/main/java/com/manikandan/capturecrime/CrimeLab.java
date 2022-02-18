package com.manikandan.capturecrime;

import android.content.Context;

import com.manikandan.capturecrime.models.Crime;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab crimeLab;
    private  static List<Crime> mCrimes;

    private CrimeLab(Context context) {
        mCrimes = new LinkedList<>();
        initialLoadCrimes(mCrimes);
    }

    private void initialLoadCrimes(List<Crime> mCrimes) {
        /*for (int i = 0; i < 5; i++) {
            Crime crime = new Crime();
            crime.setmTitle("Crime #" + i);
            crime.setMsolved(i % 2 == 0);
            mCrimes.add(crime);
        }*/
    }

    public static CrimeLab getCrimeLab(Context context) {
        if (crimeLab == null) {
            crimeLab = new CrimeLab(context);
        }
        return crimeLab;
    }

    public void addCrime(Crime crime){
        mCrimes.add(crime);
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrimeDetails(UUID uuid) {
        for (Crime mCrime : mCrimes) {
            if (mCrime.getmID().equals(uuid)) {
                return mCrime;
            }
        }
        return null;
    }

}
