package io;

import java.io.FileInputStream;

public class ExeFileReader {
	
	private FileInputStream input;
	public ExeFileReader(FileInputStream input)
	{
		this.input=input;
	}
	

	/* Read exe file header, finds sections and extract data */
	public ExeHeader read_header()
	{
		try
		{
			int offset=0;
			int line=0;
			int input_byte=0;
			char value;
			int header_size=0;
			int pe_sign_location=0;
			int section_loc;
			int optional_h_loc;
			int optional_header_size=0;
			for(int i=0;i<0x40;i++)
			{
				input_byte=input.read();
				value=(char)input_byte;
				if(i>0 && i%16==0)
				{		
					line++;
				}
					
				if(i==0x08 || i==0x09)
					header_size+=input_byte*(Math.pow(16,i-8));
				
				if(i>=0x3C && i<0x40)
					pe_sign_location+=input_byte*(Math.pow(16,i-60));
				
				
				offset++;
					
			}
			if(pe_sign_location<0x40)
			{
				pe_sign_location*=16;
			}
			
			;
			for(int i=offset;i<pe_sign_location-1;i++)
			{
				input_byte=input.read();
				offset++;
			}
			int number_of_sections=0;
			for(int i=offset; i<pe_sign_location+24;i++)
			{
				
				input_byte=input.read();
				offset++;
				value=(char)input_byte;
				section_loc = pe_sign_location + 0x06;
				optional_h_loc = pe_sign_location + 0x14;
				if(i==(section_loc) || i==(section_loc + 0x01)) //PE_header_loc + offset
					number_of_sections+=input_byte*Math.pow(16,i-section_loc);
				if(i==(optional_h_loc) || i == (optional_h_loc+ 0x01))
				{
					optional_header_size+=input_byte*(Math.pow(16,i-optional_h_loc));	
				}
			}
			for(int i = offset; i<optional_header_size+offset;i++)
			{
				input_byte=input.read();	
			}
			offset=optional_header_size+offset;
			int section=0;
			int[] section_size=new int[number_of_sections];
			int[] virtual_size=new int[number_of_sections];
			int[] raw_data_size=new int[number_of_sections];
			int[] r_pointer=new int[number_of_sections];
			//int j=0;
			int k=0;
			int v_size_k; //3byte
			int r_size_k;
			int r_pointer_k;
			
			int section_header_size=40; //40B between each header
		
			for(int i=0; i<number_of_sections;i++)
			{
				v_size_k = 0x03;
				r_size_k = 0x03;
				r_pointer_k = 0x03;
				
				for(int j=0;j<section_header_size;j++)
				{
					
					if(j>=0x08 && j<0x0C)
					{
						input_byte=input.read();
						virtual_size[i]+=(int) (input_byte*Math.pow(16,v_size_k));
						v_size_k--;
					}
					else if(j>=0x10 && j<0x14)
					{
						input_byte=input.read();
						raw_data_size[i]+=(int) (input_byte*Math.pow(16,r_size_k));
						r_size_k--;
					}
					else if(j>=0x14 && j<0x18)
					{
						input_byte=input.read();
						r_pointer[i]+=(int) (input_byte*Math.pow(16,r_pointer_k));
						r_pointer_k--;
					}
					else
					{
						input_byte=input.read();
					}
					offset++;
					
					
				}
				
			}
			return new ExeHeader(virtual_size,raw_data_size,r_pointer,number_of_sections,offset);

		}
		catch (Exception e)
		{
			return null;
		}
		
	
	}

}
