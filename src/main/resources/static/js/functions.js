(function() {
  (function($) {
    var getCurrentCheckedStatus, selectedFormat, selectedLanguage;
    if ($('#calendar').length > 0) {
      getCurrentCheckedStatus = function(checkboxId) {
        return $(checkboxId).is(':checked');
      };
      selectedLanguage = $('#calendar').data('lang');
      selectedFormat = $('#calendar').data('dateformat').replace(/d/g, 'D').replace(/y/g, 'Y');
      $('#calendar').fullCalendar({
        header: {
          left: 'prev,next today',
          center: 'title',
          right: 'month,agendaWeek'
        },
        defaultView: 'month',
        lang: selectedLanguage,
        weekends: true,
        eventLimit: true,
        weekNumbers: true,
        firstDay: 1,
        eventSources: [
          {
            url: '/calendar',
            data: function() {
              return {
                showApproved: $('#showApproved').is(':checked'),
                showPending: $('#showPending').is(':checked'),
                showRejected: $('#showRejected').is(':checked'),
                showCanceled: $('#showCanceled').is(':checked')
              };
            }
          }
        ],
        eventDataTransform: function(eventData) {
          switch (eventData.state) {
            case 'approved':
              eventData.color = '#008d4c';
              break;
            case 'pending-approvement':
              eventData.color = '#f39c12';
              break;
            case 'pending-substitute':
              eventData.color = '#f39c12';
              break;
            case 'rejected':
              eventData.color = '#dd4b39';
              break;
            case 'canceled':
              eventData.color = '#ca195a';
              break;
            default:
              eventData.color = '#00f';
          }
          eventData.displayedEnd = moment(eventData.end);
          if (eventData.allDay) {
            eventData.end = moment(eventData.end).add(1, 'days');
          }
          return eventData;
        },
        eventClick: function(calEvent, jsEvent, view) {
          var $vacationDetail, managerText, substituteText;
          if (calEvent.type === 'VACATION') {
            $vacationDetail = $('#vacationDetail');
            $vacationDetail.find('.user').text(calEvent.userFirstName + ' ' + calEvent.userLastName);
            $vacationDetail.find('.state').text(calEvent.state);
            if (calEvent.allDay) {
              $vacationDetail.find('.time').text(calEvent.start.format('DD.MM.YYYY') + ' - ' + calEvent.displayedEnd.format('DD.MM.YYYY'));
            } else {
              $vacationDetail.find('.time').text(calEvent.start.format('DD.MM.YYYY HH:mm') + ' - ' + calEvent.displayedEnd.format('DD.MM.YYYY HH:mm'));
            }
            if ((calEvent.substituteFirstName != null) && (calEvent.substituteLastName != null)) {
              substituteText = calEvent.substituteFirstName + ' ' + calEvent.substituteLastName;
            } else {
              substituteText = '-';
            }
            $vacationDetail.find('.substitute').text(substituteText);
            if ((calEvent.managerFirstName != null) && (calEvent.managerLastName != null)) {
              managerText = calEvent.managerFirstName + ' ' + calEvent.managerLastName;
            } else {
              managerText = '-';
            }
            $vacationDetail.find('.manager').text(managerText);
            $vacationDetail.show();
            return $('#sidebar').addClass('active');
          } else {
            $vacationDetail = $('#vacationDetail');
            return $vacationDetail.hide();
          }
        }
      });
      return $('#calendarFilter .checkbox input').on('click', function() {
        return $('#calendar').fullCalendar('refetchEvents');
      });
    }
  })(jQuery);

}).call(this);

(function() {
  (function($) {
    $.fn.datepicker.dates['en'] = {
      days: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
      daysShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
      daysMin: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
      months: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
      monthsShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
      today: "Today",
      clear: "Clear"
    };
    return $.fn.datepicker.dates['de'] = {
      days: ["Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"],
      daysShort: ["Son", "Mon", "Die", "Mit", "Don", "Fre", "Sam", "Son"],
      daysMin: ["So", "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"],
      months: ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"],
      monthsShort: ["Jan", "Feb", "Mär", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"],
      today: "Heute",
      clear: "Löschen",
      weekStart: 1
    };
  })(jQuery);

}).call(this);

(function() {
  var activateToggle, initializeDatePicker, refreshVacationForm, scrollToVacationForm, toggleHalfDay;

  scrollToVacationForm = function() {
    var clientWidth;
    clientWidth = document.documentElement.clientWidth;
    if (clientWidth < 761) {
      return $('html, body').animate({
        scrollTop: ($('#vacation-form-panel').offset().top)
      }, 'slow');
    }
  };

  activateToggle = function() {
    return $('.flip').click(function() {
      $('.card').toggleClass('flipped');
      setTimeout((function() {
        $('.card .front').toggleClass('invisible');
        $('.card .back').toggleClass('invisible');
      }), 180);
    });
  };

  refreshVacationForm = function(data) {
    var $myForm;
    $myForm = $('#vacation-form-panel');
    $myForm.html(data).hide().fadeIn(800);
    $('.panel-default').matchHeight();
    initializeDatePicker();
    $myForm.find('select').select2();
    scrollToVacationForm();
    activateToggle();
    return toggleHalfDay();
  };

  toggleHalfDay = function() {
    return $('#halfDay').on('change', function() {
      if ($(this).is(':checked')) {
        if (!isNaN($('.input-group.date.dateTo').datepicker('getDate').valueOf())) {
          $('.input-group.date.dateTo').datepicker('update', $('.input-group.date.dateFrom').datepicker('getDate'));
        }
        $('#dateToBox').hide();
        return $('#halfDayBox').show();
      } else {
        $('#dateToBox').show();
        return $('#halfDayBox').hide();
      }
    });
  };

  initializeDatePicker = function() {
    return $('.input-group.date').each(function() {
      var $this;
      $this = $(this);
      $this.datepicker({
        format: $this.data('dateformat').replace(/M/g, 'm'),
        autoclose: true,
        language: $this.data('lang'),
        weekStart: 1,
        daysOfWeekDisabled: '0,6',
        todayHighlight: true
      });
      if ($this.hasClass('dateFrom')) {
        return $this.on('changeDate', function() {
          if (isNaN($('.input-group.date.dateTo').datepicker('getDate').valueOf()) || $('#halfDay').is(':checked')) {
            return $('.input-group.date.dateTo').datepicker('update', $(this).datepicker('getDate'));
          }
        });
      }
    });
  };

  (function($) {
    initializeDatePicker();
    $('[data-toggle="tooltip"]').tooltip();
    $('.panel-default select').select2();
    $('.autosubmit').on('change', function() {
      return $(this).closest('form').submit();
    });
    $('.panel-default').matchHeight();
    activateToggle();
    $('[data-toggle="filter"]').click(function() {
      return $('.offcanvas-filter').toggleClass('active');
    });
    $('.edit-vacation a, .approve-vacation a, #newVacation').click(function() {
      $.ajax({
        url: $(this).attr('href'),
        dataType: "html",
        error: function(jqXHR, textStatus, errorThrown) {
          return console.log(textStatus);
        },
        success: function(data) {
          return refreshVacationForm(data);
        }
      });
      return false;
    });
    return toggleHalfDay();
  })(jQuery);

}).call(this);

(function() {
  (function($) {
    $('[data-toggle="tooltip"]').tooltip();
    $('#employees').select2();
    $('#employees').off('select2-opening');
    $('.select2-search__field').focus();
    $('#employees').on('select2:unselecting', function(e) {
      $(this).data('state', 'unselected');
    });
    $('#employees').on('select2:opening ', function(e) {
      if ($(this).data('state') === 'unselected') {
        $(this).removeData('state');
        e.preventDefault();
      }
    });
    return $('#clear').on('click', function(e) {
      $('#employees').val(null).trigger('change');
    });
  })(jQuery);

}).call(this);
