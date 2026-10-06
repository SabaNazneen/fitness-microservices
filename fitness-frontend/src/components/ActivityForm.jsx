import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import { duration, TextField } from '@mui/material';
import React, { useState } from 'react';
const ActivityForm = () => {
  const [activity,setActivity] = useState({
    type: "RUNNING", duration: '',caloriesBurned: '',
    additionalMetrics:{}
  });

  return (
    <Box component="form" sx={{ mb: 2 }}>
      <Box sx={{ minWidth: 120 }}>
        <FormControl fullWidth sx={{mb:2}}>
          <InputLabel >Activity Type</InputLabel>
          <Select
            value={activity.type}
onChange={(e) => {setActivity({...activity,type:e.target.value})}}          >
            <MenuItem value="RUNNING">Running</MenuItem>
            <MenuItem value="WALKING">Walking</MenuItem>
            <MenuItem value="CYCLING">Cycling</MenuItem>
          </Select>
        </FormControl>
      </Box>
      <TextField fullWidth
      label="Calories Burned"
      type='number'
      sx={{mb:2}}
      value={activity.caloriesBurned}
      onChange={(e)=>{setActivity({...activity,duration:e.target.value})}}
      />
    </Box>
  );
};

export default ActivityForm;