package me.elsiff.holomedals.datahandler;

import java.util.List;
import java.util.UUID;
import me.elsiff.holomedals.Medal;

public interface UserDataHandler {
   void giveMedal(UUID var1, Medal var2);

   void takeMedal(UUID var1, Medal var2);

   List<Medal> getMedals(UUID var1);

   void takeAllMedals(Medal var1);

   String getDisplayMedals(UUID var1);

   void setDisplayMedals(UUID var1, String var2);
}
