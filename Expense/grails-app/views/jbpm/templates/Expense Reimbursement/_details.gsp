<table>
    <tr class="prop">
        <td class="name" valign="top">
            <label for="customer">Purpose: </label>
        </td>
        <td clas="value" valign="top">
            <input type="text" id="purpose"
                   name="${jbpm.ctxVariable(path:'expenseClaim.purpose')}"
                    value="${expenseClaim?.purpose?.encodeAsHTML()}"/>
        </td>
    </tr>
    <tr class="prop">
        <td class="name" valign="top">
            <label for="product">Date:</label>
        </td>
        <td clas="value" valign="top">
            <g:datePicker name="${jbpm.ctxVariable(path:'expenseClaim.date')}" value="${expenseClaim?.date}" precision="day"/>
        </td>
    </tr>
    <tr class="prop"> 
        <td class="name" valign="top"><label for="product">Amount:</label> </td> 
        <td clas="value" valign="top"> 
            <input type="text" id="amount" name="${jbpm.ctxVariable(path:'expenseClaim.amount')}"   
                    value="${expenseClaim?.amount?.encodeAsHTML()}"/> 
         </td> 
    </tr> 
</table>
