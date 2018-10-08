const HelperFunctions = {
  getDate(calendar) {
    return new Date(
      calendar.year,
      calendar.month,
      calendar.dayOfMonth,
      calendar.hourOfDay,
      calendar.minute,
      calendar.second,
    )
  },
};
