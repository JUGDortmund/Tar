#Scroll to manualEntry-form in small resolutions
scrollToVacationManualEntryForm = ->
  clientWidth = document.documentElement.clientWidth;
  if clientWidth < 761 then $('html, body').animate({ scrollTop: ($('#entry-form-panel').offset().top - 20)}, 'slow')

showManualEntryForm = (data) ->
    $myFilter = $('#filter-form-panel')
    $myFilter.hide()
    $myForm = $('#entry-form-panel')
    $myForm.html(data).hide().show()
    scrollToVacationManualEntryForm()
    $('.offcanvas-filter').show()
    $myForm.find('select.strict').select2({
      minimumResultsForSearch: Infinity
    })

    $myForm.find('select.searchable').select2()
    $('#year').on 'change', ->
        form = $(this).closest('form')
        sendAjaxFormRequest(form.attr( "action" ), form.serialize() , "POST")

    $('#submitEntry').on 'click', () ->
        form = $(this).closest('form')
        $.ajax
            url: form.attr( "action" )
            data: form.serialize()
            dataType: "html"
            method: "POST"
            error: (jqXHR, textStatus, errorThrown) ->
                if(jqXHR.status == 500)
                    showManualEntryForm(jqXHR.responseText)
                else
                    console.log(errorThrown)
            success: (data) ->
                refreshActiveTable(data)
                hideManualEntryForm()
        return false


    $('#saveManualEntry').submit (e) ->
        e.preventDefault(e)

hideManualEntryForm = () ->
    $myForm = $('#entry-form-panel')
    $myForm.hide( )
    $myFilter = $('#filter-form-panel')
    $myFilter.hide().show()

refreshActiveTable = (data) ->
    panelToRefresh = $('.panel-collapse:visible').closest( ".panel" )
    panelToRefresh.replaceWith(data)
    index = $('input[name="index"]').val()
    $('#collapse'+index).addClass("in")
    $('.manual-entry').click ->
        sendAjaxFormRequest($(this).attr('href'), null, "GET")

sendAjaxFormRequest = (url, data, method) ->
    $.ajax
        url: url
        data: data
        dataType: "html"
        method: method
        error: (jqXHR, textStatus, errorThrown) ->
            console.log(textStatus)
        success: (data) ->
            showManualEntryForm(data)
    return false

# document ready
(($) ->
    hideManualEntryForm()
    $('[data-toggle="tooltip"]').tooltip()
    $('#employees').select2()
    $('#employees').off('select2-opening')
    $('.select2-search__field').focus()

    $('#employees').on 'select2:unselecting', (e) ->
        $(this).data 'state', 'unselected'
        return

    $('#employees').on 'select2:opening ', (e) ->
        if ($(this).data('state') == 'unselected')
            $(this).removeData 'state'
            e.preventDefault()
        return

    $('#clear').on 'click', (e) ->
        $('#employees').val(null).trigger('change')
        return

    $('.manual-entry').click ->
        sendAjaxFormRequest($(this).attr('href'), null, "GET")

)(jQuery);