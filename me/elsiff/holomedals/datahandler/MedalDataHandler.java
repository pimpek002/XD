package me.elsiff.holomedals.datahandler;

import java.util.List;
import me.elsiff.holomedals.Medal;

public interface MedalDataHandler {
   List<Medal> loadMedals();

   void createMedal(Medal var1);

   void updateMedal(Medal var1);

   void removeMedal(Medal var1);
}
