/*
 *  Copyright (C) 2024 Evolveum
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.evolveum.gizmo.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkingDaysProvider {

    private List<HolidayDay> holidays;

    public WorkingDaysProvider() {
        initHolidays();
    }

    private void initHolidays() {
        holidays = new ArrayList<>();
        holidays.add(new HolidayDay(1, 1, "Deň vzniku Slovenskej republiky"));
        holidays.add(new HolidayDay(1, 6, "Traja králi"));
        holidays.add(new HolidayDay(5, 1, "Sviatok práce"));
        holidays.add(new HolidayDay(5, 8, "Deň víťazstva nad fašizmom"));
        holidays.add(new HolidayDay(7, 5, "Sviatok svätého Cyrila a Metoda"));
        holidays.add(new HolidayDay(8, 29, "Výročie SNP"));
        holidays.add(new HolidayDay(9, 1, "Deň Ústavy Slovenskej republiky"));
        holidays.add(new HolidayDay(9, 15, "Sedembolestná Panna Mária"));
        holidays.add(new HolidayDay(11, 1, "Sviatok všetkých svätých"));
        holidays.add(new HolidayDay(17, 1, "Deň boja za slobodu a demokraciu"));
        holidays.add(new HolidayDay(12, 24, "Štedrý deň"));
        holidays.add(new HolidayDay(12, 25, "Prvý sviatok vianočný"));
        holidays.add(new HolidayDay(12, 26, "Druhý sviatok vianočný"));
    }

    public List<HolidayDay> getPublicHolidaysFor(int year, int month) {

        List<HolidayDay> summarizedHolidays = new ArrayList<>(holidays);
        summarizedHolidays.addAll(getEasterHoliday(year));

        return summarizedHolidays
                .stream()
                .filter(holidayDay -> holidayDay.getMonth() == month)
                .toList();
    }

    private List<HolidayDay> getEasterHoliday(int year) {
        List<HolidayDay> easterHolidays = new ArrayList<>();
        LocalDate easterSunday = calculateEasterDate(year);
        easterHolidays.add(new HolidayDay(easterSunday.getMonthValue(), easterSunday.getDayOfMonth(), "Veľkonočná nedeľa"));

        LocalDate easterMonday = easterSunday.plusDays(1);
        easterHolidays.add(new HolidayDay(easterMonday.getMonthValue(), easterMonday.getDayOfMonth(), "Veľká noc"));

        LocalDate goodFriday = easterSunday.minusDays(2);
        easterHolidays.add(new HolidayDay(goodFriday.getMonthValue(), goodFriday.getDayOfMonth(), "Veľký piatok"));

        return easterHolidays;
    }

    public static LocalDate calculateEasterDate(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;

        return LocalDate.of(year, month, day);
    }
}
