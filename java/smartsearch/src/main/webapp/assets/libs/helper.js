const Formatter = {
  getCalendar(date) {
    return {
      year: date.getUTCFullYear(),
      month: date.getUTCMonth(),
      dayOfMonth: date.getUTCDate(),
      hourOfDay: date.getUTCHours(),
      minute: date.getUTCMinutes(),
      second: date.getUTCSeconds(),
    };
  },
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
  telephone(tel) {
    const telChars = String(tel).split('');

    if (telChars.length === 10) {
      const dd = telChars.slice(0, 2);
      const firstPart = telChars.slice(2, 6);
      const lastPart = telChars.slice(6);
      return `(${dd.join('')}) ${firstPart.join('')}-${lastPart.join('')}`;
    }
    return tel;
  },
};