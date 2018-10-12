const Formatter = {
  date(calendar) {
    return new Date(
      calendar.year,
      calendar.month,
      calendar.dayOfMonth,
      calendar.hourOfDay,
      calendar.minute,
      calendar.second,
    ).toLocaleDateString();
  },
  datetime(calendar) {
    return new Date(
      calendar.year,
      calendar.month,
      calendar.dayOfMonth,
      calendar.hourOfDay,
      calendar.minute,
      calendar.second,
    ).toLocaleString();
  },
  time(calendar) {
    return new Date(
      calendar.year,
      calendar.month,
      calendar.dayOfMonth,
      calendar.hourOfDay,
      calendar.minute,
      calendar.second,
    ).toLocaleTimeString()
  },
  fullDate(calendar) {
    const date = new Date(
      calendar.year,
      calendar.month,
      calendar.dayOfMonth,
      calendar.hourOfDay,
      calendar.minute,
      calendar.second,
    );

    return new Intl.DateTimeFormat('pt-BR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    }).format(date);
  },
  currency(money) {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(money);
  },
  decimal(number) {
    return new Intl.NumberFormat('pt-BR', {
      style: 'decimal',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(number)
  },
};
