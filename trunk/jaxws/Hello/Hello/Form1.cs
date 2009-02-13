using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Hello
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void btnSubmit_Click(object sender, EventArgs e)
        {
            ConcatenatorService.ConcatenatorClient service = new ConcatenatorService.ConcatenatorClient();
            txtResult.Text = service.concatenate(txtString1.Text, txtString2.Text);
        }
    }
}
